/*
 * Copyright 2017-2022 FUJITSU CLOUD TECHNOLOGIES LIMITED All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.nifcloud.mbaas.core

import org.json.JSONException
import org.json.JSONObject

/**
 * Access Control (ACL) feature class.
 *
 * This class is do features on settings Access Control of data for NCMB Kotlin SDK.
 *
 */
class NCMBAcl {
    /**
     * ACL for user/public
     */
    private val userAcl: MutableMap<String, Permission>

    /**
     * ACL for role
     */
    private val roleAcl: MutableMap<String, Permission>

    companion object {
        /** public ACL  */
        const val ACL_PUBLIC = "*"

        /** prefix of role  */
        const val PREFIX_ROLE = "role:"
    }

    /**
     * Constructor with class name
     * @param className class name for data store
     */
    constructor() {
        userAcl = HashMap()
        roleAcl = HashMap()
    }

    /**
     * Constructor for NCMBAcl from JSON
     *
     * @param json json object of defalut acl
     * @throws JSONException exception from JSONObject
     */
    constructor(json: JSONObject) : this() {
        parse(json)
    }

    /**
     * Inner class of representation for permission
     */
    class Permission {
        /** access permitted to read  */
        val readable: Boolean

        /** access permitted to write  */
        val writable: Boolean

         /**
         * Constructor
         */
        constructor(){
             readable = false
             writable = false
        }

        /**
         * Constructor
         * @param read read permission
         * @param write write permission
         */
        constructor(read: Boolean, write: Boolean) {
            /** access permitted to read and write */
            readable = read
            writable = write
        }

        /**
         * Construct for NCMBAcl from JSON
         */
        constructor(json: JSONObject) {
            readable = json.optBoolean("read", false)
            writable = json.optBoolean("write", false)
        }

        /**
         * Represent to JSON object
         * @return JSONObject JSON representation
         */
        @Throws(JSONException::class)
        fun toJson(): JSONObject {
            val json = JSONObject()
            try {
                if (readable) {
                    json.put("read", true)
                }
                if (writable) {
                    json.put("write", true)
                }
            } catch (e: JSONException) {
                // do nothing
            }
            return json
        }

        /**
         * Represent to string
         * @return String string representation
         */
        override fun toString(): String {
            return toJson().toString()
        }
    }

    /**
     * parse json from JSONObject
     * @param input input JSONObject
     * @throws JSONException exception from JSONObject
     */
    @Throws(JSONException::class)
    internal fun parse(input: JSONObject) {
        for (id in input.keys()) {
            val json = input.getJSONObject(id)
            val permission = Permission(json)
            if (checkRoleName(id)) {
                val roleName = roleNameFromId(id)
                roleAcl[roleName] = permission
            } else {
                userAcl[id] = permission
            }
        }
    }

    internal fun checkRoleName(id: String):Boolean{
        return id.startsWith(PREFIX_ROLE)
    }

    @Throws(Exception::class)
    internal fun roleNameFromId(id: String): String {
        var roleName = id.substring(PREFIX_ROLE.length)
        return roleName
    }

    /**
     * Convert to JSONObject
     * @return converted value
     * @throws JSONException exception from JSONException
     */
    @Throws(JSONException::class)
    internal fun toJson(): JSONObject {
        var result = JSONObject()
        for ((userKey, value) in userAcl) {
            result.put(userKey, value.toJson())
        }
        for ((key, value) in roleAcl) {
            val roleKey = addRoleKey(key)
            result.put(roleKey, value.toJson())
        }
        return result
    }

    fun addRoleKey(key: String): String {
        val roleKey = PREFIX_ROLE + key
        return roleKey
    }

    /**
     * Check ACL is empty or not
     * @return return true when acl is empty
     */
    val isEmpty: Boolean
        get() = userAcl.isEmpty() && roleAcl.isEmpty()

    /**
     * get user access permissions
     * @param userId user id
     * @return permissions
     */
    fun getUserAccess(userId: String): Permission {
        var permission = userAcl[userId]
        if (permission == null) {
            permission = Permission()
            // not set permission to ACL
        }
        return permission
    }

    /**
     * set user access permissions
     * @param userId user id
     * @param permission permission
     */
    fun setUserAccess(userId: String, permission: Permission) {
        userAcl[userId] = permission
    }

    /**
     * get role access permissions
     * @param roleName role name
     * @return permissions
     */
    fun getRoleAccess(roleName: String): Permission {
        var permission = roleAcl[roleName]
        if (permission == null) {
            permission = Permission()
            // not set permission to ACL
        }
        return permission
    }

    /**
     * set role access permissions
     * @param roleName role name
     * @param permission permission
     */
    fun setRoleAccess(roleName: String, permission: Permission) {
        roleAcl[roleName] = permission
    }

    /**
     * Remove permission for user (after set at local OR fetch from server)
     * @param userId user id
     * @return success to remove permission
     */
    fun removeUserPermission(userId: String): Boolean {
        var result = false
        if (userAcl.containsKey(userId)) {
            userAcl.remove(userId)
            result = true
        }
        return result
    }

    /**
     * Remove permisson for role (after set at local OR fetch from server)
     * @param roleName role name
     * @return success to remove permission
     */
    fun removeRolePermission(roleName: String): Boolean {
        var result = false
        if (roleAcl.containsKey(roleName)) {
            roleAcl.remove(roleName)
            result = true
        }
        return result
    }

    // readable/writable shortcut methods
    /**
     * Get whether the given user id is allowed to read
     * @param userId user id
     * @return can read or not
     */
    fun getUserReadAccess(userId: String): Boolean {
        return getUserAccess(userId).readable
    }

    /**
     * Set whether the given user id is allowed to read.
     * @param userId user id
     * @param allowed can read or not
     */
    fun setUserReadAccess(userId: String, allowed: Boolean) {
        val p = getUserAccess(userId)
        if (p.readable == allowed) {
            // already set
            return
        }
        val newPermission = Permission(allowed, p.writable)
        setUserAccess(userId, newPermission)
    }

    /**
     * Get whether the given user id is allowed to write
     * @param userId user id
     * @return can write or not
     */
    fun getUserWriteAccess(userId: String): Boolean {
        return getUserAccess(userId).writable
    }

    /**
     * Set whether the given user is allowed to write.
     * @param userId user id
     * @param allowed can write or not
     */
    fun setUserWriteAccess(userId: String, allowed: Boolean) {
        val p = getUserAccess(userId)
        if (p.writable == allowed) {
            // already set
            return
        }
        val newPermission = Permission(p.readable, allowed)
        setUserAccess(userId, newPermission)
    }

    /**
     * Get whether the gevin role name is allowed to read
     * @param roleName role name
     * @return can read or not
     */
    fun getRoleReadAccess(roleName: String): Boolean {
        return getRoleAccess(roleName).readable
    }

    /**
     * Set whether the given role is allowed to read.
     * @param roleName role name
     * @param allowed can read or not
     */
    fun setRoleReadAccess(roleName: String, allowed: Boolean) {
        val p = getRoleAccess(roleName)
        if (p.readable == allowed) {
            // already set
            return
        }
        val newPermission = Permission(allowed, p.writable)
        setRoleAccess(roleName, newPermission)
    }

    /**
     * Get whether the gevin role name is allowed to write
     * @param roleName role name
     * @return can write or not
     */
    fun getRoleWriteAccess(roleName: String): Boolean {
        return getRoleAccess(roleName).writable
    }

    /**
     * Set whether the given role is allowed to write.
     * @param roleName role name
     * @param allowed can write or not
     */
    fun setRoleWriteAccess(roleName: String, allowed: Boolean) {
        val p = getRoleAccess(roleName)
        if (p.writable == allowed) {
            // already set
            return
        }
        val newPermission = Permission(p.readable, allowed)
        setRoleAccess(roleName, newPermission)
    }

    // public ACL shortcuts
    /**
     * Get whether the public is allowed to read.
     * @return can read or not
     */
    /**
     * Set whether the public is allowed to read.
     * @param allowed can read or not
     */
    var publicReadAccess: Boolean
        get() = getUserReadAccess(ACL_PUBLIC)
        set(allowed) {
            setUserReadAccess(ACL_PUBLIC, allowed)
        }
    /**
     * Get whether the public is allowed to write.
     * @return can write or not
     */
    /**
     * Set whether the public is allowed to write.
     * @param allowed can write or not
     */
    var publicWriteAccess: Boolean
        get() = getUserWriteAccess(ACL_PUBLIC)
        set(allowed) {
            setUserWriteAccess(ACL_PUBLIC, allowed)
        }
}
