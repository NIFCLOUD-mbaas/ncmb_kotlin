import com.nifcloud.mbaas.core.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

/**
 * NCMBQuery is used to search data from NIFCLOUD mobile backend
 */
class NCMBQuery<T : NCMBBase?>(private val mClassName: String) {
    private var mWhereConditions: JSONObject? = JSONObject()

    /**
     * search data from NIFCLOUD mobile backend
     * @return NCMBObject(include extend class) list of search result
     * @throws NCMBException exception sdk internal or NIFCLOUD mobile backend
     */
    @Throws(NCMBException::class)
    fun find(): List<T> {
        return if (mClassName == "user") {
            val userServ = NCMBUserService()
            userServ.findUser(conditions) as List<T>
        } else {
            val objServ = NCMBObjectService()
            objServ.findObject(mClassName, conditions)
        }
    }

    /**
     * search data from NIFCLOUD mobile backend asynchronously
     * @param callback executed callback after data search
     */
    fun findInBackground(callback: NCMBCallback<T>) {
        if (mClassName == "user") {
            val userServ = NCMBUserService()
            userServ.findUserInBackground(conditions, object : SearchUserCallback() {
                fun done(users: ArrayList<NCMBUser>?, e: NCMBException?) {
                    callback.done(users as List<T>?, e)
                }
            })
        } else {
            val objServ = NCMBObjectService()
            objServ.findObjectInBackground(
                mClassName,
                conditions,
                object : FindObjectCallback() {
                    fun done(objects: List<NCMBObject>?, e: NCMBException?) {
                        callback.done(objects as List<T>?, e)
                    }
                })
        }
    }

    /**
     * get current search condition
     * @return current search condition
     */
    val conditions: JSONObject?
        get() {
            val conditions = JSONObject()
            return try {
                if (mWhereConditions != null && mWhereConditions.length() > 0) {
                    conditions.put("where", mWhereConditions)
                }
                conditions
            } catch (e: JSONException) {
                null
            }
        }

    @Throws(JSONException::class)
    private fun convertConditionValue(value: Any): Any {
        return if (value is Date) {
            val dateJson = JSONObject("{'__type':'Date'}")
            val df: SimpleDateFormat = getIso8601()
            dateJson.put("iso", df.format(value as Date))
            dateJson
        } else if (value is List<*>) {
            val gson = Gson()
            JSONArray(gson.toJson(value))
        } else if (value is Map<*, *>) {
            val gson = Gson()
            JSONObject(gson.toJson(value))
        } else {
            value
        }
    }

    /**
     * set the conditions to search the data that matches the value of the specified key
     * @param key field name to set the conditions
     * @param value condition value
     */
    fun whereEqualTo(key: String?, value: Any) {
        try {
            mWhereConditions!!.put(key, convertConditionValue(value))
        } catch (e: JSONException) {
            throw IllegalArgumentException(e.message)
        }
    }

    /**
     * Constructor
     * @param className class name string for search data
     */
    init {
        mWhereConditions = JSONObject()
    }
}