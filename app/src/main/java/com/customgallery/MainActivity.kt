package com.customgallery

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.customgallery.GalleryView.adapter.GridViewAdapter
import com.customgallery.GalleryView.adapter.SelectedImageRcyAdapter
import com.customgallery.GalleryView.model.ItemModel
import com.customgallery.GalleryView.viewModel.GalleryViewModel
import com.customgallery.GalleryView.viewModel.GalleryViewModelFactory
import com.customgallery.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity(),SelectedImageRcyAdapter.Callback,GridViewAdapter.Callback {

    var title: String? = null
    var viewModel:GalleryViewModel ?= null
    lateinit var binding:ActivityMainBinding
    var adapter:GridViewAdapter ?= null
    var list:MutableList<ItemModel> = mutableListOf()
    var listFolders:MutableList<FolderNameWithPathModel?> = mutableListOf()

    var adapterSelected:SelectedImageRcyAdapter ?= null
    var listSelected:MutableList<ItemModel> = mutableListOf()
    val MAX_SELECTION_COUNT = 10

    val REQUEST_WRITE_PERMISSION = 101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_main)

        title = "All Photos"

        initRecycler()

        /**
         * permission storage
         */
        requestPermission()

        /**
         * back pressed called
         */
        binding.layoutToolbar.imgClose.setOnClickListener {
            onBackPressed()
        }
    }

    /**
     * read/write storage permission
     */
    private fun requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(arrayOf(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE), REQUEST_WRITE_PERMISSION)
        } else {

            /**
             * if permission already granted
             */
            initObserver()
        }
    }

    /**
     * permission result
     */
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_WRITE_PERMISSION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                /**
                 * if permission granted
                 */
                initObserver()
            } else {

                /**
                 * if permission not granted
                 */
                requestPermission()
            }
        }
    }

    /**
     * this method is used for init recycler
     */
    private fun initRecycler() {
        /**
         * grid image rcy
         */
        adapter = GridViewAdapter(this,list,this)
        binding.rcyImages.adapter = adapter

        /**
         * selected rcy
         */
        adapterSelected = SelectedImageRcyAdapter(this, listSelected,this)
        binding.rcyImageSelected.adapter = adapterSelected
    }

    /**
     * notify images recycler
     */
    private fun notifyData() {
        if (adapter != null) {
            adapter!!.notifyDataSetChanged()
        }
    }

    /**
     * notify images recycler from position
     */
    private fun notifyItemPosition(pos:Int) {
        if (adapter != null) {
            adapter!!.notifyItemChanged(pos)
        }
    }

    /**
     * notify selected rcy
     */
    private fun notifySelectedRcy() {
        /**
         * if selected image count grater or less
         */
        if(listSelected.size > 0) {
            binding.horizontalLine.visibility = View.VISIBLE
            binding.rcyImageSelected.visibility = View.VISIBLE
            binding.layoutToolbar.imgDone.visibility = View.VISIBLE
        } else {
            binding.horizontalLine.visibility = View.GONE
            binding.rcyImageSelected.visibility = View.GONE
            binding.layoutToolbar.imgDone.visibility = View.INVISIBLE
        }

        if (adapterSelected != null) {
            adapterSelected!!.list = listSelected
            adapterSelected!!.notifyDataSetChanged()
        }
    }

    /**
     * this method is used for init spinner and its listener
     */
    private fun initSpinner(list:ArrayList<String>) {
        val arrAdapter = ArrayAdapter<String>(this, R.layout.layout_spinner_item, list)
        binding.layoutToolbar.spnrFolder.adapter = arrAdapter

        binding.layoutToolbar.spnrFolder.onItemSelectedListener = object :AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(p0: AdapterView<*>?) {}

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {

                /**
                 * if other option selection from spinner
                 * then clear selection list and notify
                 */
                listSelected.clear()
                notifySelectedRcy()

                /**
                 * if postion is grater than 0 then get data from special folder otherwise get all images
                 */
                if (p2 > 0) {
                    if (viewModel != null) {
                        viewModel!!.getImageListFromFolder(listFolders.get(p2-1)!!.path)
                    }
                } else {
                    if (viewModel != null) {
                        viewModel!!.allImages(this@MainActivity)
                    }
                }
            }
        }


    }


    /**
     * this method is used for init viewModel and observe response
     */
    private fun initObserver() {
        /**
         * init viewModel
         */
        viewModel = ViewModelProvider(this,GalleryViewModelFactory(this)).get(GalleryViewModel::class.java)

        /**
         * observe
         */
        viewModel!!.errObserve.observe(this, Observer {
            System.out.println(getString(it))
        })

        viewModel!!.errMsgObserve.observe(this, Observer {
            System.out.println(it)
        })

        viewModel!!.folderList.observe(this, Observer {
            if (it != null && it.size > 0) {
                this.listFolders.addAll(it)


                val listItem:ArrayList<String> = ArrayList()
                listItem.add("ALL")
                it.forEach {
                    listItem.add(it!!.folderName.toUpperCase())
                }
                initSpinner(listItem)
            }

            /**
             * all images request
             */
            viewModel!!.allImages(this)

        })

        viewModel!!.imagesList.observe(this, Observer {
            this.list.clear()
            if (it != null) {
                val list:MutableList<ItemModel> = mutableListOf()
                it.forEach {
                    list.add(ItemModel(Uri.fromFile(it),false,0))
                }
                this.list.addAll(list)
                notifyData()
            }
        })

        viewModel!!.getFolderNameWithPath(this)
    }

    override fun onItemRemoved(position: Int) {
        val count = listSelected.get(position).count
        val posAllImages = listSelected.get(position).position

        /**
         * remove from selected image
         */
        listSelected.removeAt(position)

        /**
         * update selected main rcy
         */
        list.get(posAllImages).isSelected = false
        notifyItemPosition(posAllImages)

        /**
         * update selected
         */
        updateSelectionCount(count)

        /**
         * notify selected rcy
         */
        notifySelectedRcy()
    }

    override fun onItemSelected(position: Int) {
        val item = list.get(position)

        if (item.isSelected) {
            listSelected.remove(item)
            item.isSelected = false
            val count = item.count

            /**
             * update selected count
             */
            updateSelectionCount(count)

        } else {
            if (listSelected.size < MAX_SELECTION_COUNT) {
                item.isSelected = true
                item.count = listSelected.size + 1
                listSelected.add(item)
            } else {
                Toast.makeText(this,"Max image selected.",Toast.LENGTH_SHORT).show()
            }
        }
        notifyItemPosition(position)
        notifySelectedRcy()
    }

    private fun updateSelectionCount(count:Int) {
        listSelected.forEach {
            if (it.count >= count) {
                it.count = it.count - 1
                notifyItemPosition(it.position)
            }
        }
    }
}