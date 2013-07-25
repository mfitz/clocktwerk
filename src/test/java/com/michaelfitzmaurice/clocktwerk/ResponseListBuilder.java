/**
 *    Copyright 2013 Michael Fitzmaurice
 * 
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 * 
 *        http://www.apache.org/licenses/LICENSE-2.0
 * 
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package com.michaelfitzmaurice.clocktwerk;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import twitter4j.RateLimitStatus;
import twitter4j.ResponseList;
import twitter4j.Status;

public class ResponseListBuilder {

    private ResponseList<Status> responseList;
    private final List<Status> statusList = new ArrayList<Status>();
    
    @SuppressWarnings("serial")
    public ResponseListBuilder() {
        responseList = new ResponseList<Status>() {

            @Override
            public int getAccessLevel() {
                return 0;
            }

            @Override
            public boolean add(Status arg0) {
                return statusList.add(arg0);
            }

            @Override
            public void add(int arg0, Status arg1) {
                statusList.add(arg0, arg1);
            }

            @Override
            public boolean addAll(Collection<? extends Status> arg0) {
                return statusList.addAll(arg0);
            }

            @Override
            public boolean addAll(int arg0, Collection<? extends Status> arg1) {
                return statusList.addAll(arg0, arg1);
            }

            @Override
            public void clear() {
                statusList.clear();
            }

            @Override
            public boolean contains(Object arg0) {
                return statusList.contains(arg0);
            }

            @Override
            public boolean containsAll(Collection<?> arg0) {
                return statusList.containsAll(arg0);
            }

            @Override
            public Status get(int arg0) {
                return statusList.get(arg0);
            }

            @Override
            public int indexOf(Object arg0) {
                return statusList.indexOf(arg0);
            }

            @Override
            public boolean isEmpty() {
                return statusList.isEmpty();
            }

            @Override
            public Iterator<Status> iterator() {
                return statusList.iterator();
            }

            @Override
            public int lastIndexOf(Object arg0) {
                return statusList.lastIndexOf(arg0);
            }

            @Override
            public ListIterator<Status> listIterator() {
                return statusList.listIterator();
            }

            @Override
            public ListIterator<Status> listIterator(int arg0) {
                return statusList.listIterator(arg0);
            }

            @Override
            public boolean remove(Object arg0) {
                return statusList.remove(arg0);
            }

            @Override
            public Status remove(int arg0) {
                return statusList.remove(arg0);
            }

            @Override
            public boolean removeAll(Collection<?> arg0) {
                return statusList.remove(arg0);
            }

            @Override
            public boolean retainAll(Collection<?> arg0) {
                return statusList.retainAll(arg0);
            }

            @Override
            public Status set(int arg0, Status arg1) {
                return statusList.set(arg0, arg1);
            }

            @Override
            public int size() {
                return statusList.size();
            }

            @Override
            public List<Status> subList(int arg0, int arg1) {
                return statusList.subList(arg0, arg1);
            }

            @Override
            public Object[] toArray() {
                return statusList.toArray();
            }

            @Override
            public <T> T[] toArray(T[] arg0) {
                return statusList.toArray(arg0);
            }

            @Override
            public RateLimitStatus getRateLimitStatus() {
                return null;
            }
        };
    }

    public static ResponseListBuilder aResponseList() {
        return new ResponseListBuilder();
    }
    
    public ResponseListBuilder withStatus(Status status) {
        responseList.add(status);
        return this;
    }
    
    public ResponseList<Status> build() {
        return responseList;
    }
}
