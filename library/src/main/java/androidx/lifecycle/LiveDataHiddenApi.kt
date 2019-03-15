package androidx.lifecycle

object LiveDataHiddenApi {
    fun LiveData<*>.version(): Int {
        return this.version
    }
}