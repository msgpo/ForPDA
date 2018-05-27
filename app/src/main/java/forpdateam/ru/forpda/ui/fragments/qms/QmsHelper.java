package forpdateam.ru.forpda.ui.fragments.qms;

import java.util.Observer;

import forpdateam.ru.forpda.App;
import forpdateam.ru.forpda.common.simple.SimpleObservable;
import forpdateam.ru.forpda.entity.app.TabNotification;
import forpdateam.ru.forpda.entity.common.MessageCounters;
import forpdateam.ru.forpda.entity.db.qms.QmsContactBd;
import forpdateam.ru.forpda.entity.db.qms.QmsThemeBd;
import forpdateam.ru.forpda.entity.db.qms.QmsThemesBd;
import forpdateam.ru.forpda.entity.remote.events.NotificationEvent;
import forpdateam.ru.forpda.model.CountersHolder;
import io.reactivex.disposables.Disposable;
import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by radiationx on 22.10.17.
 */

public class QmsHelper {
    private static QmsHelper instance = null;
    private SimpleObservable qmsEvents = new SimpleObservable();

    public static void init() {
        if (instance != null) {
            throw new IllegalStateException("Already init");
        }
        instance = new QmsHelper();
    }

    public static QmsHelper get() {
        if (instance == null) {
            throw new IllegalStateException("Not init");
        }
        return instance;
    }

    public QmsHelper() {
        Disposable disposable = App.get().Di().getEventsRepository()
                .observeEventsTab()
                .subscribe(this::handleEvent);
    }

    private void handleEvent(TabNotification event) {
        Realm realm = Realm.getDefaultInstance();
        RealmResults<QmsThemesBd> themes = realm
                .where(QmsThemesBd.class)
                .findAll();
        QmsThemeBd targetTheme = null;
        QmsThemesBd targetDialog = null;
        for (QmsThemesBd dialog : themes) {
            for (QmsThemeBd theme : dialog.getThemes()) {
                if (theme.getId() == event.getEvent().getSourceId()) {
                    targetDialog = dialog;
                    targetTheme = theme;
                    break;
                }
            }
            if (targetTheme != null) {
                break;
            }
        }

        if (targetTheme != null) {
            QmsThemeBd finalTargetTheme = targetTheme;
            QmsThemesBd finalTargetDialog = targetDialog;
            realm.executeTransaction(realm1 -> {
                if (event.isWebSocket()) {
                    if (NotificationEvent.isRead(event.getType())) {
                        finalTargetTheme.setCountNew(0);
                    }
                } else {
                    if (NotificationEvent.isNew(event.getType())) {
                        finalTargetTheme.setCountNew(event.getEvent().getMsgCount());
                    }
                }

                QmsContactBd contact = realm1
                        .where(QmsContactBd.class)
                        .equalTo("id", finalTargetDialog.getUserId())
                        .findFirst();

                if (contact != null) {
                    int count = 0;
                    for (QmsThemeBd theme : finalTargetDialog.getThemes()) {
                        count += theme.getCountNew();
                    }
                    contact.setCount(count);
                }

            });
        }

        int globalCount = 0;
        for (NotificationEvent ev : event.getLoadedEvents()) {
            globalCount += ev.getMsgCount();
        }

        CountersHolder countersHolder = App.get().Di().getCountersHolder();
        MessageCounters counters = countersHolder.get();
        counters.setQms(globalCount);
        countersHolder.set(counters);

        realm.close();
        notifyQms(event);
    }

    public void subscribeQms(Observer observer) {
        qmsEvents.addObserver(observer);
    }

    public void unSubscribeQms(Observer observer) {
        qmsEvents.deleteObserver(observer);
    }

    public void notifyQms(TabNotification event) {
        qmsEvents.notifyObservers(event);
    }
}
