package pl.wp.dogs.breeds_list

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.setPadding
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import io.reactivex.Observable
import io.reactivex.disposables.SerialDisposable
import io.reactivex.subjects.BehaviorSubject
import pl.wp.dogs.R
import javax.inject.Inject

class BreedsListFragment @Inject constructor(private val presenter: BreedsListContract.Presenter) :
    Fragment(R.layout.fragment_breeds_list), BreedsListContract.View {
    private val breedsSubject = BehaviorSubject.createDefault(emptyList<Breed>())
    private val breedClicksSubject = BehaviorSubject.create<Breed>()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        presenter.onAttach(this)
    }

    override fun onDetach() {
        presenter.onDetach()
        super.onDetach()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<RecyclerView>(R.id.breeds_list_recycler_view).apply {
            adapter = BreedAdapter(breedsSubject, breedClicksSubject::onNext)
            layoutManager = LinearLayoutManager(context)
        }
    }

    override fun showBreeds(dogs: List<Breed>) {
        breedsSubject.onNext(dogs)
    }

    override fun showError(error: Throwable) {
        Toast.makeText(requireContext(), "Error: ${error.message}", Toast.LENGTH_LONG).show()
    }

    override val breedClicks: Observable<Breed> = breedClicksSubject.hide()
}

private class BreedViewHolder(private val title: TextView) : ViewHolder(title) {
    fun setTitle(text: String) {
        title.text = text
    }

    fun setOnClickListener(onClick: () -> Unit) {
        title.setOnClickListener { onClick() }
    }
}

private class BreedAdapter(
    private val breeds: Observable<List<Breed>>,
    private val onBreedClick: (Breed) -> Unit
) :
    RecyclerView.Adapter<BreedViewHolder>() {
    private val serialDisposable = SerialDisposable()
    private var data = emptyList<Breed>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BreedViewHolder {
        return TextView(parent.context).apply {
            setPadding(36)
        }.let(::BreedViewHolder)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: BreedViewHolder, position: Int) {
        holder.setTitle(data[position].name)
        holder.setOnClickListener {
            onBreedClick(data[position])
        }
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        breeds.subscribe(::onData).also(serialDisposable::set)
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        serialDisposable.dispose()
    }

    private fun onData(newData: List<Breed>) {
        data = newData
        notifyDataSetChanged()
    }
}
