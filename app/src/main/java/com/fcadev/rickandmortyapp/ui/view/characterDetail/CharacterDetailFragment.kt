package com.fcadev.rickandmortyapp.ui.view.characterDetail

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.fcadev.rickandmortyapp.R
import com.fcadev.rickandmortyapp.databinding.FragmentCharacterDetailBinding

class CharacterDetailFragment : Fragment() {

    private var _binding: FragmentCharacterDetailBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCharacterDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getCharacterDetails()
    }

    private fun getCharacterDetails() {
        val bundle = arguments
        if (bundle != null) {
            val args = CharacterDetailFragmentArgs.fromBundle(bundle)

            val imageUrl = args.characterImage
            Glide.with(requireContext()).load(imageUrl)
                .centerCrop()
                .into(binding.ivCharacterDetailImage)

            val editEpisode = if (args.characterEpisodes.toString().length > 19) {
                args.characterEpisodes.toString().substring(0, 19) + "..."
            } else {
                args.characterEpisodes.toString()
            }

            if (args.characterEpisodes.toString().length > 19) {
                binding.ivAllButton.visibility = View.VISIBLE
            } else {
                binding.ivAllButton.visibility = View.GONE
            }

            if (args.characterStatus.toString() == "Dead") {
                binding.ivIsAliveDot.setImageResource(R.drawable.reddot)
            } else if (args.characterStatus.toString() == "Alive") {
                binding.ivIsAliveDot.setImageResource(R.drawable.greendot)
            } else {
                binding.ivIsAliveDot.setImageResource(R.drawable.dot)
            }

            binding.tvCharacterDetailName.text = args.characterName.toString()
            binding.tvStatusContent.text = getCharacterEditingResult(args.characterStatus.toString())
            binding.tvGenderContent.text = getCharacterEditingResult(args.characterGender.toString())
            binding.tvCreatedAtContent.text = args.characterCreated.toString()
            binding.tvOriginContent.text = getCharacterEditingResult(args.characterOrigin.toString())
            binding.tvLocationContent.text = getCharacterEditingResult(args.characterLocation.toString())
            binding.tvSpecyContent.text = getCharacterEditingResult(args.characterSpecy.toString())
            binding.tvEpisodesContent.text = editEpisode

            binding.ivAllButton.setOnClickListener {
                binding.tvEpisodesContent.text = args.characterEpisodes.toString()
                binding.ivAllButton.visibility = View.GONE
            }
        }
    }

    fun getCharacterEditingResult (text: String): String {
        return if (text.length > 17){
            text.substring(0, 17) + "..."
        }else {
            text
        }
    }
}