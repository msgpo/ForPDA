package forpdateam.ru.forpda.model.repository.profile

import android.support.annotation.DrawableRes
import android.support.annotation.StringRes
import forpdateam.ru.forpda.R
import forpdateam.ru.forpda.model.data.remote.api.profile.Profile
import forpdateam.ru.forpda.entity.remote.profile.ProfileModel
import forpdateam.ru.forpda.model.SchedulersProvider
import io.reactivex.Observable

/**
 * Created by radiationx on 02.01.18.
 */

class ProfileRepository(
        private val schedulers: SchedulersProvider,
        private val profileApi: Profile
) {

    fun loadProfile(url: String): Observable<ProfileModel> = Observable
            .fromCallable { profileApi.getProfile(url) }
            .subscribeOn(schedulers.io())
            .observeOn(schedulers.ui())

    fun saveNote(note: String): Observable<Boolean> = Observable
            .fromCallable { profileApi.saveNote(note) }
            .subscribeOn(schedulers.io())
            .observeOn(schedulers.ui())

    @StringRes
    fun getTypeString(type: ProfileModel.ContactType): Int {
        when (type) {
            ProfileModel.ContactType.QMS -> return R.string.profile_contact_qms
            ProfileModel.ContactType.WEBSITE -> return R.string.profile_contact_site
            ProfileModel.ContactType.ICQ -> return R.string.profile_contact_icq
            ProfileModel.ContactType.TWITTER -> return R.string.profile_contact_twitter
            ProfileModel.ContactType.JABBER -> return R.string.profile_contact_jabber
            ProfileModel.ContactType.VKONTAKTE -> return R.string.profile_contact_vk
            ProfileModel.ContactType.GOOGLE_PLUS -> return R.string.profile_contact_google_plus
            ProfileModel.ContactType.FACEBOOK -> return R.string.profile_contact_facebook
            ProfileModel.ContactType.INSTAGRAM -> return R.string.profile_contact_instagram
            ProfileModel.ContactType.MAIL_RU -> return R.string.profile_contact_mail_ru
            ProfileModel.ContactType.TELEGRAM -> return R.string.profile_contact_telegram
            ProfileModel.ContactType.WINDOWS_LIVE -> return R.string.profile_contact_windows_live
            else -> return R.string.undefined
        }
    }

    @StringRes
    fun getTypeString(type: ProfileModel.InfoType): Int {
        when (type) {
            ProfileModel.InfoType.REG_DATE -> return R.string.profile_info_reg
            ProfileModel.InfoType.ALERTS -> return R.string.profile_info_alerts
            ProfileModel.InfoType.ONLINE_DATE -> return R.string.profile_info_last_online
            ProfileModel.InfoType.GENDER -> return R.string.profile_info_gender
            ProfileModel.InfoType.BIRTHDAY -> return R.string.profile_info_birthday
            ProfileModel.InfoType.USER_TIME -> return R.string.profile_info_user_time
            ProfileModel.InfoType.CITY -> return R.string.profile_info_city
            else -> return R.string.undefined
        }
    }

    @StringRes
    fun getTypeString(type: ProfileModel.StatType): Int {
        when (type) {
            ProfileModel.StatType.SITE_KARMA -> return R.string.profile_stat_site_karma
            ProfileModel.StatType.SITE_POSTS -> return R.string.profile_stat_site_posts
            ProfileModel.StatType.SITE_COMMENTS -> return R.string.profile_stat_site_comments
            ProfileModel.StatType.FORUM_REPUTATION -> return R.string.profile_stat_forum_reputation
            ProfileModel.StatType.FORUM_TOPICS -> return R.string.profile_stat_forum_topics
            ProfileModel.StatType.FORUM_POSTS -> return R.string.profile_stat_forum_posts
            else -> return R.string.undefined
        }
    }

    @DrawableRes
    fun getContactIcon(type: ProfileModel.ContactType): Int {
        when (type) {
            ProfileModel.ContactType.QMS -> return R.drawable.contact_qms
            ProfileModel.ContactType.WEBSITE -> return R.drawable.contact_site
            ProfileModel.ContactType.ICQ -> return R.drawable.contact_icq
            ProfileModel.ContactType.TWITTER -> return R.drawable.contact_twitter
            ProfileModel.ContactType.JABBER -> return R.drawable.contact_jabber
            ProfileModel.ContactType.VKONTAKTE -> return R.drawable.contact_vk
            ProfileModel.ContactType.GOOGLE_PLUS -> return R.drawable.contact_google_plus
            ProfileModel.ContactType.FACEBOOK -> return R.drawable.contact_facebook
            ProfileModel.ContactType.INSTAGRAM -> return R.drawable.contact_instagram
            ProfileModel.ContactType.MAIL_RU -> return R.drawable.contact_mail_ru
            ProfileModel.ContactType.TELEGRAM -> return R.drawable.contact_telegram
        /*case WINDOWS_LIVE:
                return R.drawable.contact_site;*/
            else -> return R.drawable.contact_site
        }
    }
}
