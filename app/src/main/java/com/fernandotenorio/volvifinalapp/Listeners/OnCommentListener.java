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

package com.fernandotenorio.volvifinalapp.Listeners;

public interface OnCommentListener {
    void onComment(long nbComments);
    void onFailed(String error);
}
