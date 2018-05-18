package forpdateam.ru.forpda.presentation.articles.detail

import com.arellomobile.mvp.InjectViewState
import forpdateam.ru.forpda.common.Utils
import forpdateam.ru.forpda.common.mvp.BasePresenter
import forpdateam.ru.forpda.entity.remote.news.DetailsPage
import forpdateam.ru.forpda.model.interactors.news.ArticleInteractor
import forpdateam.ru.forpda.presentation.ILinkHandler
import forpdateam.ru.forpda.presentation.IRouter

/**
 * Created by radiationx on 11.11.17.
 */

@InjectViewState
class ArticleDetailPresenter(
        private val articleInteractor: ArticleInteractor,
        private val router: IRouter,
        private val linkHandler: ILinkHandler
) : BasePresenter<ArticleDetailView>() {

    var currentData: DetailsPage? = null

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        loadArticle()
        articleInteractor
                .observeData()
                .subscribe({
                    currentData = it
                })
                .addToDisposable()
    }

    fun loadArticle() {
        articleInteractor
                .loadArticle()
                .doOnTerminate { viewState.setRefreshing(true) }
                .doAfterTerminate { viewState.setRefreshing(false) }
                .subscribe({
                    viewState.showArticle(it)
                }, {
                    it.printStackTrace()
                })
                .addToDisposable()
    }

    fun openAuthorProfile() {
        currentData?.let {
            linkHandler.handle("https://4pda.ru/forum/index.php?showuser=${it.authorId}", router)
        }
    }

    fun copyLink() {
        currentData?.let {
            Utils.copyToClipBoard("https://4pda.ru/index.php?p=${it.id}")
        }
    }

    fun shareLink() {
        currentData?.let {
            Utils.shareText("https://4pda.ru/index.php?p=${it.id}")
        }
    }

    fun createNote() {
        currentData?.let {
            val url = "https://4pda.ru/index.php?p=${it.id}"
            viewState.showCreateNote(it.title, url)
        }
    }

}
