// Copyright 2017 Google Inc.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//    http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package codeu.model.data;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.datastore.Blob;
import com.google.appengine.api.datastore.Text;
import com.google.appengine.api.images.ImagesServiceFactory;
import com.google.appengine.api.images.ServingUrlOptions;
import com.google.appengine.repackaged.com.google.api.client.util.Base64;
import java.io.UnsupportedEncodingException;
import java.time.Instant;
import java.util.UUID;

/** Class representing a registered user. */
public class User {
  private final UUID id;
  private final String name;
  private final String passwordHash;
  private final Instant creation;
  private String aboutMe;
  private Instant lastLogin;
  private BlobKey avatarKey;

  /**
   * Constructs a new User.
   *  @param id the ID of this User
   * @param name the username of this User
   * @param passwordHash the password hash of this User
   * @param creation the creation time of this User
   */
  public User(UUID id, String name, String passwordHash, Instant creation) {
    this.id = id;
    this.name = name;
    this.passwordHash = passwordHash;
    this.creation = creation;
    this.lastLogin = creation;
    this.aboutMe = " ";
  }

  /** Returns the ID of this User. */
  public UUID getId() {
    return id;
  }

  /** Returns the username of this User. */
  public String getName() {
    return name;
  }

  /** Returns the password hash of this User. */
  public String getPasswordHash() {
    return passwordHash;
  }

  /** Returns the creation time of this User. */
  public Instant getCreationTime() {
    return creation;
  }
  //Returns the about me of this User.
  public String getAboutMe(){
     return aboutMe;
  }
  //Sets users about me content
  public void setAboutMe(String aboutMe){
    this.aboutMe = aboutMe;
  }

  /** Returns is the user was active yesterday or not. */
  public Instant getLastLogin(){
    return lastLogin;
  }

  /** Returns is the user was active yesterday or not. */
  public void setLastLogin(Instant login){
    this.lastLogin = login;
  }

  public BlobKey getAvatarKey() {
    return avatarKey;
  }

  public void setAvatarKey(BlobKey avatarKey) {
    this.avatarKey = avatarKey;
  }

  public String getAvatarImage() {
    if(avatarKey == null) return null;
    ServingUrlOptions options = ServingUrlOptions.Builder
        .withBlobKey(avatarKey);
    return ImagesServiceFactory.getImagesService().getServingUrl(options);
  }
}
