package cz.data

interface IKeyable<K>{
    fun getKey(): K
    fun getKeySize(): Int
}