package forpdateam.ru.forpda.presentation.qms.chat

import android.os.Bundle
import com.arellomobile.mvp.InjectViewState
import forpdateam.ru.forpda.apirx.ForumUsersCache
import forpdateam.ru.forpda.common.IntentHandler
import forpdateam.ru.forpda.common.mvp.BasePresenter
import forpdateam.ru.forpda.entity.app.TabNotification
import forpdateam.ru.forpda.entity.remote.editpost.AttachmentItem
import forpdateam.ru.forpda.entity.remote.events.NotificationEvent
import forpdateam.ru.forpda.entity.remote.qms.QmsChatModel
import forpdateam.ru.forpda.entity.remote.qms.QmsMessage
import forpdateam.ru.forpda.model.data.remote.api.RequestFile
import forpdateam.ru.forpda.model.repository.qms.QmsRepository
import forpdateam.ru.forpda.ui.TabManager
import forpdateam.ru.forpda.ui.fragments.TabFragment
import forpdateam.ru.forpda.ui.fragments.qms.QmsThemesFragment
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * Created by radiationx on 11.11.17.
 */

@InjectViewState
class QmsChatPresenter(
        private val qmsRepository: QmsRepository
) : BasePresenter<QmsChatView>(), IQmsChatPresenter {

    var themeId = 0
    var userId = 0
    var title: String? = null
    var nick: String? = null
    var avatarUrl: String? = null

    var currentData: QmsChatModel? = null

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        nick?.let { nick -> title?.let { title -> viewState.setTitles(title, nick) } }
        tryShowAvatar()
        loadChat()
    }

    fun loadChat() {
        qmsRepository
                .getChat(userId, themeId)
                .doOnTerminate { viewState.setRefreshing(true) }
                .doAfterTerminate { viewState.setRefreshing(false) }
                .subscribe({
                    currentData = it
                    viewState.showChat(it)
                    tryShowAvatar()
                }, {
                    it.printStackTrace()
                })
                .addToDisposable()
    }

    fun sendNewTheme(nick: String, title: String, message: String) {
        qmsRepository
                .sendNewTheme(nick, title, message)
                .doOnTerminate { viewState.setRefreshing(true) }
                .doAfterTerminate { viewState.setRefreshing(false) }
                .subscribe({
                    currentData = it
                    viewState.onNewThemeCreate(it)
                    viewState.showChat(it)
                    tryShowAvatar()
                }, {
                    it.printStackTrace()
                })
                .addToDisposable()
    }

    fun sendMessage(message: String) {
        qmsRepository
                .sendMessage(userId, themeId, message)
                .doOnTerminate { viewState.setMessageRefreshing(true) }
                .doAfterTerminate { viewState.setMessageRefreshing(false) }
                .subscribe({
                    viewState.onSentMessage(it)
                }, {
                    it.printStackTrace()
                })
                .addToDisposable()
    }

    fun blockUser() {
        currentData?.nick?.let { nick ->
            qmsRepository
                    .blockUser(nick)
                    .map { it.firstOrNull { it.nick == nick } != null }
                    .subscribe({
                        viewState.onBlockUser(it)
                    }, {
                        it.printStackTrace()
                    })
                    .addToDisposable()
        }
    }

    private fun tryShowAvatar() {
        Observable
                .fromCallable {
                    var result: String? = null
                    result = avatarUrl?.let { it } ?: result
                    result = currentData?.avatarUrl?.let { it } ?: result
                    if (result == null) {
                        currentData?.nick?.let {
                            result = ForumUsersCache.loadUserByNick(it).avatar
                        }
                    }
                    result
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    it?.let { viewState.showAvatar(it) }
                }, { })
                .addToDisposable()
    }


    fun uploadFiles(files: List<RequestFile>, pending: List<AttachmentItem>) {
        qmsRepository
                .uploadFiles(files, pending)
                .subscribe({
                    viewState.onUploadFiles(it)
                }, {
                    it.printStackTrace()
                })
                .addToDisposable()
    }

    fun handleEvent(event: TabNotification) {
        val themeId = event.event.sourceId
        val messageId = event.event.messageId
        currentData?.let {
            if (themeId == it.themeId) {
                when (event.type) {
                    NotificationEvent.Type.NEW -> {
                        onNewWsMessage(themeId, messageId)
                    }
                    NotificationEvent.Type.READ -> {
                        viewState.makeAllRead()
                    }
                    NotificationEvent.Type.MENTION -> {
                    }
                    NotificationEvent.Type.HAT_EDITED -> {
                    }
                    null -> {
                    }
                }
            }
        }

    }

    private fun onNewWsMessage(themeId: Int, messageId: Int) {
        currentData?.let {
            val lastMessId = it.messages.lastOrNull()?.id ?: 0
            qmsRepository
                    .getMessagesFromWs(themeId, messageId, lastMessId)
                    .subscribe({
                        onNewMessages(it)
                    }, {
                        it.printStackTrace()
                    })
                    .addToDisposable()
        }
    }

    fun checkNewMessages() {
        currentData?.let {
            val lastMessId = it.messages.lastOrNull()?.id ?: 0
            qmsRepository
                    .getMessagesAfter(themeId, it.themeId, lastMessId)
                    .subscribe({
                        onNewMessages(it)
                    }, {
                        it.printStackTrace()
                    })
                    .addToDisposable()
        }
    }

    private fun onNewMessages(items: List<QmsMessage>) {
        currentData?.let { data ->
            val result = items.filter { new ->
                data.messages.find { it.id != new.id } != null
            }
            data.messages.addAll(result)
            viewState.onNewMessages(result)
        }
    }

    fun createThemeNote() {
        currentData?.let {
            val url = "https://4pda.ru/forum/index.php?act=qms&mid=${it.userId}&t=${it.themeId}"
            viewState.showCreateNote(it.title, it.nick, url)
        }
    }

    fun openProfile() {
        currentData?.let {
            IntentHandler.handle("https://4pda.ru/forum/index.php?showuser=${it.userId}")
        }
    }

    fun openDialogs() {
        currentData?.let {
            val args = Bundle()
            args.putString(TabFragment.ARG_TITLE, it.nick)
            args.putInt(QmsThemesFragment.USER_ID_ARG, it.userId)
            args.putString(QmsThemesFragment.USER_AVATAR_ARG, it.avatarUrl)
            TabManager.get().add(QmsThemesFragment::class.java, args)
        }
    }

    fun onSendClick() {
        if (themeId == QmsChatModel.NOT_CREATED) {
            viewState.temp_sendNewTheme()
        } else {
            viewState.temp_sendMessage()
        }
    }

    override fun loadMoreMessages() {
        currentData?.let {
            val endIndex = it.showedMessIndex
            val startIndex = Math.max(endIndex - 30, 0)
            it.showedMessIndex = startIndex
            viewState.showMoreMessages(it.messages, startIndex, endIndex)
        }
    }
}