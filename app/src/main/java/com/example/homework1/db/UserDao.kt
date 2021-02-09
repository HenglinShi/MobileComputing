package com.example.homework1.db

import androidx.room.*

@Dao
interface UserDao {
    @Transaction
    @Insert
    fun insert(userInfo: UserInfo): Long

    @Query("DELETE FROM userInfo WHERE uid = :id")
    fun delete(id: Int)

    @Query("SELECT * FROM userInfo")
    fun getPassword(): UserInfo

    @Query( "SELECT password FROM userInfo WHERE username = :username")
    fun getPassword(username: String): String

    @Query("SELECT uid FROM userInfo WHERE username =  :username")
    fun getUid(username: String): Int

    @Query("SELECT * FROM userInfo WHERE username =  :username")
    fun getUserInfos(username: String): List<UserInfo>


    @Query("SELECT * FROM userInfo WHERE uid =  :uid")
    fun getUserInfos(uid: Int): List<UserInfo>


    @Update
    fun updateUser(userInfo: UserInfo) : Int

}