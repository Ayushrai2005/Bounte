package com.ayush.bounte.DashBoard

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.Query
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Collections
import java.util.Date
import

class DashBoardFragment : Fragment() {
    private var binding: FragmentDashboardBinding? = null
    private var arr_recent_lofo: ArrayList<DashBoardViewModel>? = null
    private var adapter: RecyclerRecentLoFoAdapter? = null
    private var db: FirebaseFirestore? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.getRoot()

        //image slider
        val imageSlider: ImageSlider = root.findViewById(R.id.imageSlider)
        val slideModels: ArrayList<SlideModel> = ArrayList<SlideModel>()
        slideModels.add(SlideModel(R.drawable.dashboard_img1, ScaleTypes.FIT))
        slideModels.add(SlideModel(R.drawable.dashboard_img2, ScaleTypes.FIT))
        //        slideModels.add(new SlideModel(R.drawable.dashboard_img3, ScaleTypes.FIT));
//        slideModels.add(new SlideModel(R.drawable.dashboard_img4, ScaleTypes.FIT));
        imageSlider.setImageList(slideModels, ScaleTypes.FIT)
        val recentLostFoundList: RecyclerView =
            root.findViewById<RecyclerView>(R.id.recent_lost_found_list)
        //        recentLostFoundList.setLayoutManager(new LinearLayoutManager(requireContext()));
        arr_recent_lofo = ArrayList<DashBoardViewModel>()
        val gridLayoutManager =
            GridLayoutManager(requireContext(), 2, GridLayoutManager.VERTICAL, false)
        recentLostFoundList.setLayoutManager(gridLayoutManager)
        adapter = RecyclerRecentLoFoAdapter(requireContext(), arr_recent_lofo)
        recentLostFoundList.setAdapter(adapter)
        db = FirebaseFirestore.getInstance()

        // Query the 'lostItems' collection
        val lostItemsQuery: Query = db.collection("lostItems")

        // Query the 'foundItems' collection
        val foundItemsQuery: Query = db.collection("foundItems")

        // Execute the queries for both lost and found items
        lostItemsQuery.get()
            .addOnSuccessListener(OnSuccessListener<QuerySnapshot> { lostItemsSnapshot: QuerySnapshot ->
                foundItemsQuery.get().addOnSuccessListener(
                    OnSuccessListener<QuerySnapshot> { foundItemsSnapshot: QuerySnapshot ->
                        // Create a list to store the combined items
                        var mergedItems: MutableList<DocumentSnapshot> =
                            ArrayList<DocumentSnapshot>()

                        // Add all lost and found items to the mergedItems list
                        mergedItems.addAll(lostItemsSnapshot.getDocuments())
                        mergedItems.addAll(foundItemsSnapshot.getDocuments())

                        // Sort the merged items by date in descending order
                        Collections.sort<DocumentSnapshot>(
                            mergedItems,
                            java.util.Comparator<DocumentSnapshot> { o1: DocumentSnapshot, o2: DocumentSnapshot ->
                                val dateLostString1: String = o1.getString("dateLost")
                                val dateFoundString1: String = o1.getString("dateFound")
                                val dateLostString2: String = o2.getString("dateLost")
                                val dateFoundString2: String = o2.getString("dateFound")
                                val dateFormat = SimpleDateFormat("dd/MM/yyyy")
                                var date1: Date? = null
                                var date2: Date? = null
                                if (dateLostString1 != null) {
                                    try {
                                        date1 = dateFormat.parse(dateLostString1)
                                    } catch (e: ParseException) {
                                        e.printStackTrace()
                                    }
                                } else if (dateFoundString1 != null) {
                                    try {
                                        date1 = dateFormat.parse(dateFoundString1)
                                    } catch (e: ParseException) {
                                        e.printStackTrace()
                                    }
                                }
                                if (dateLostString2 != null) {
                                    try {
                                        date2 = dateFormat.parse(dateLostString2)
                                    } catch (e: ParseException) {
                                        e.printStackTrace()
                                    }
                                } else if (dateFoundString2 != null) {
                                    try {
                                        date2 = dateFormat.parse(dateFoundString2)
                                    } catch (e: ParseException) {
                                        e.printStackTrace()
                                    }
                                }
                                if (date1 != null && date2 != null) {
                                    return@sort date2.compareTo(date1)
                                } else if (date1 != null) {
                                    return@sort -1
                                } else if (date2 != null) {
                                    return@sort 1
                                }
                                0
                            })

                        // Limit the list to 5 items
                        if (mergedItems.size > 10) {
                            mergedItems = mergedItems.subList(0, 10)
                        }

                        // Now, you have the top 5 most recent items from both collections in descending order
                        for (item in mergedItems) {
                            val lofo: DashBoardViewModel =
                                item.toObject(DashBoardViewModel::class.java)
                            if (lofo != null) {
                                arr_recent_lofo!!.add(lofo)
                            }
                        }
                        adapter.notifyDataSetChanged()
                    })
            })
        adapter.setOnItemClickListener(object : OnItemClickListener() {
            fun onItemClick(item: DashBoardViewModel) {
                // Handle the item click here
                val selectedItemName: String = item.getItemName()
                // Create an Intent and navigate to LostDetails activity with the selected item name
                val intent: Intent
                if (item.getTag().equalsIgnoreCase("lost")) {
                    intent = Intent(requireContext(), LostDetails::class.java)
                    intent.putExtra("itemId", selectedItemName)
                } else {
                    intent = Intent(requireContext(), FoundDetails::class.java)
                    intent.putExtra("itemId", selectedItemName)
                }
                startActivity(intent)
            }
        })

//        recentLostFoundList.setAdapter(adapter);
        // Get the currently logged in user
        val user: FirebaseUser = FirebaseAuth.getInstance().getCurrentUser()
        val userName = root.findViewById<TextView>(R.id.userName) // Replace with your TextView's ID
        if (user != null) {
            val email: String = user.getEmail() // Get the user's email
            val db: FirebaseFirestore = FirebaseFirestore.getInstance()
            val usersCollectionRef: CollectionReference = db.collection("users")
            val query: Query = usersCollectionRef.whereEqualTo("email", email)
            query.get()
                .addOnCompleteListener(OnCompleteListener<QuerySnapshot> { task: Task<QuerySnapshot> ->
                    if (task.isSuccessful()) {
                        for (document in task.getResult()) {
                            val name: String = document.getString("name")
                            if (name != null) {
                                userName.text = name // Set the user's name in the TextView
                            }
                        }
                    } else {
                        Log.d("FirebaseDebug", "Error getting documents: ", task.getException())
                    }
                })
        } else {
            Log.d("FirebaseDebug", "No user currently logged in.")
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}
