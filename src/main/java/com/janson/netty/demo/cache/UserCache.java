package com.janson.netty.demo.cache;

import io.netty.util.Recycler;

/**
 * @Description: User对象的复用
 * @Author: Janson
 * @Date: 2021/9/1 9:17
 **/
public class UserCache {

    private static final Recycler<User> USER_RECYCLER = new Recycler<User>() {
        @Override
        protected User newObject(Handle<User> handle) {
            return new User(handle);
        }
    };


    static final class User {
        private String name;
        private Recycler.Handle<User> handle;

        public User(Recycler.Handle<User> handle) {
            this.handle = handle;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Recycler.Handle<User> getHandle() {
            return handle;
        }

        public void setHandle(Recycler.Handle<User> handle) {
            this.handle = handle;
        }

        public void recycle() {
            handle.recycle(this);
        }
    }

    public static void main(String[] args) {
        User user1 = USER_RECYCLER.get();
        user1.setName("hello");
        user1.recycle();

        User user2 = USER_RECYCLER.get();
        System.out.println(user2.getName());
        System.out.println(user1 == user2);

    }

}
