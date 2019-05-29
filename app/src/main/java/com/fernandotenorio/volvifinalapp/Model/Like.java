/*
 *
 * Copyright 2019 OURA Olivier Baudouin, Software Architect at Minlessika (Abidjan, Côte d'Ivoire)
 * https://www.minlessika.com
 * Email Pro : baudolivier.oura@minlessika.com
 * Home email : baudolivier.oura@gmail.com
 * Phone number : +225 07622999
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
 *
 */
package com.fernandotenorio.volvifinalapp.Model;

import com.fernandotenorio.volvifinalapp.Utils.DataSnapshotPrinter;
import com.google.firebase.database.DataSnapshot;

import java.util.HashMap;
import java.util.Map;

public class Like {

    private final String id;
    private final String blogId;
    private final String authorId;
    private final long createdDate;

    public Like(final DataSnapshot snapshot) {

        DataSnapshotPrinter printer = new DataSnapshotPrinter(snapshot);

        this.id = snapshot.getKey();
        this.blogId = printer.print("blogId", String.class);
        this.authorId = printer.print("authorId", String.class);
        this.createdDate = printer.print("createdDate", Long.class);
    }

    public Like(final String id, final String blogId, final String authorId, final long createdDate) {
        this.id = id;
        this.blogId = blogId;
        this.authorId = authorId;
        this.createdDate = createdDate;
    }

    public String id() {
        return id;
    }

    public String blogId() {
        return blogId;
    }

    public String authorId() {
        return authorId;
    }

    public long createdDate() {
        return createdDate;
    }

    Map<String, Object> toMap(){
        Map<String, Object> map = new HashMap<>();
        map.put("blogId", blogId);
        map.put("authorId", authorId);
        map.put("createdDate", System.currentTimeMillis());

        return map;
    }
}
