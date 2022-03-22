import android.app.Application
import android.content.ContentResolver
import android.content.ContentUris
import android.database.ContentObserver
import android.database.Cursor
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mkao.camerax.Photo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class GalleryViewModel(application: Application) : AndroidViewModel(application) {
    private val appContext: Application = application
    private var contentObserver: ContentObserver
    val photos = MutableLiveData<List<Photo>>()
    var photoToDelete: Photo? = null

    init {
        contentObserver = getApplication<Application>()
            .contentResolver.registerObserver(MediaStore.Images.Media.EXTERNAL_CONTENT_URI) {
            loadPhotos()
            }
    }

    fun loadPhotos() =
        viewModelScope.launch(Dispatchers.IO) {val projection = arrayOf(MediaStore.Images.Media._ID)
            val selection =MediaStore.Images.Media.DATE_ADDED
            val sortOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC"
            appContext.contentResolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, selection,
                null,
                sortOrder )?.use { cursor ->
                photos.postValue(addPhotosFromCursor(cursor)) }
        }
    private fun addPhotosFromCursor(cursor: Cursor): List<Photo> {
        val photoList = mutableListOf<Photo>()
        while (cursor.moveToNext()) {
            val id = cursor.getLong(0)
            val contentUri =ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
            val photo = Photo(id, contentUri)
            photoList += photo
        }
        return photoList }

    private fun ContentResolver.registerObserver(uri: Uri, observer: (selfChange: Boolean) -> Unit )
    : ContentObserver {
        val contentObserver = object :
            ContentObserver(Handler(Looper.getMainLooper() )) {
            override fun onChange(selfChange: Boolean) {
                observer(selfChange)
            }}
        registerContentObserver(uri, true, contentObserver)
        return contentObserver
    }
    override fun
            onCleared() {
        appContext.contentResolver.unregisterContentObserver(contentObserver)
    }

}
