package com.verygoodsecurity.vgscollect.core.storage

import com.verygoodsecurity.vgscollect.core.model.state.VGSFieldState

/**
 * Interface definition of storage for states of VGS fields.
 *
 * @version 1.0.0
 */
interface VgsStore<K, T> {

    /**
     * Clears all custom data which added before.
     */
    fun clear()

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