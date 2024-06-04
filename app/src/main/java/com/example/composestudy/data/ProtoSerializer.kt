package com.example.composestudy.data

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.example.composestudy.userListProto
import com.google.protobuf.InvalidProtocolBufferException
import java.io.InputStream
import java.io.OutputStream

object UserListSerializer : Serializer<userListProto> {
    override val defaultValue: userListProto
        get() = userListProto.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): userListProto {
        try {
            return userListProto.parseFrom(input)
        } catch (ex: InvalidProtocolBufferException) {
            throw CorruptionException("error: ", ex)
        }
    }

    override suspend fun writeTo(t: userListProto, output: OutputStream) {
        t.writeTo(output)
    }
}