package pl.wp.dogs.breeds_list

import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class BreedsListPresenter @Inject constructor(
    private val interactor: BreedsListContract.Interactor,
    private val routing: BreedsListContract.Routing
) : BreedsListContract.Presenter {
    private val compositeDisposable = CompositeDisposable()

    override fun onAttach(view: BreedsListContract.View) {
        interactor.getBreeds()
            .observeOn(AndroidSchedulers.mainThread())
            .doOnError(view::showError)
            .observeOn(Schedulers.io())
            .onErrorResumeNext {
                interactor.reportError(it)
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnError(view::showError)
                    .onErrorComplete()
                    .andThen(Single.just(emptyList()))
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(view::showBreeds, view::showError)
            .also(compositeDisposable::add)

        view.breedClicks
            .flatMapCompletable {
                routing.goToBreed(it)
                    .doOnError(view::showError)
                    .onErrorResumeNext(interactor::reportError)
                    .doOnError(view::showError)
                    .onErrorComplete()
            }
            .subscribe({}, view::showError)
            .also(compositeDisposable::add)
    }

    override fun onDetach() {
        compositeDisposable.clear()
    }
}
