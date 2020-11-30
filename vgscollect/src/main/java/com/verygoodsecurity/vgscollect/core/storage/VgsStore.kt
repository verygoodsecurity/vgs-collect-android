package com.verygoodsecurity.vgscollect.core.storage

/**
 * Interface definition of storage for states of VGS fields.
 */
internal interface VgsStore<K, T> {

    /**
     * Clears all custom data which added before.
     */
    fun clear()

    /**
     * Remove field from storage
     */
    fun remove(id: K)

    /**
     * Appends the specified state to the storage.
     */
    fun addItem(id: K, newState: T)

    /**
     * Returns the states of all fields bonded before to VGSCollect.
     *
     * @return the list with all fields states.
     */
    fun getItems(): MutableCollection<T>
}