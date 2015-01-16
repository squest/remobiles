(ns alfa.user
  (:require [alfa.db :as db]))

(defn exists?
  "Check whether the user exist in database"
  []
  (db/get-item "user"))

(defn save!
  "Save the user data into database"
  [username password]
  (db/set-item! "user"
                {:username username
                 :password password}))

(defn get
  "Get the user info from database"
  []
  (db/get-item "user"))



