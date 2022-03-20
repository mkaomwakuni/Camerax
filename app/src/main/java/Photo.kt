import android.net.Uri
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
//@Parcelize annotation and extends the Parcelable  interface
//it can be packaged & transported between different areas of the app


data class Photo(val id: Long,
                 val uri: Uri) : Parcelable
// id value  is unique to each Photo object, while the uri variable  contain
// URI object that details the location of the image file