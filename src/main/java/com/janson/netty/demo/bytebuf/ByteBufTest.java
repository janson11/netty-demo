package com.janson.netty.demo.bytebuf;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.Unpooled;

/**
 * @Description:
 * @Author: Janson
 * @Date: 2021/8/28 10:23
 **/
public class ByteBufTest {

    public static void main(String[] args) {
        ByteBuf byteBuf = ByteBufAllocator.DEFAULT.buffer(6,10);
        printByteBufInfo("ByteBufAllocator.buffer(5,10)",byteBuf);
        byteBuf.writeBytes(new byte[]{1,2});
        printByteBufInfo("write 2 Bytes",byteBuf);
        byteBuf.writeInt(100);
        printByteBufInfo("write Int 100",byteBuf);
        byteBuf.writeBytes(new byte[]{3,4,5});
        printByteBufInfo("write 3 Bytes",byteBuf);

        byte[] read = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(read);
        printByteBufInfo("readBytes("+byteBuf.readableBytes()+")",byteBuf);

        printByteBufInfo("BeforeGetAndSet",byteBuf);
        System.out.println("getInt(2):"+byteBuf.getInt(2));
        byteBuf.setByte(1,0);
        System.out.println("getByte(1):"+byteBuf.getByte(1));
    }

    private static void printByteBufInfo(String step,ByteBuf byteBuf){
        System.out.println("------"+step+"-----");
        System.out.println("readerIndex():"+byteBuf.readerIndex());
        System.out.println("writerIndex():"+byteBuf.writerIndex());
        System.out.println("isReadable():"+byteBuf.isReadable());
        System.out.println("isWritable():"+byteBuf.isWritable());
        System.out.println("readableBytes():"+byteBuf.readableBytes());
        System.out.println("writableBytes():"+byteBuf.writableBytes());
        System.out.println("maxWritableBytes():"+byteBuf.maxWritableBytes());
        System.out.println("capacity():"+byteBuf.capacity());
        System.out.println("maxCapacity():"+byteBuf.maxCapacity());



    }
}
