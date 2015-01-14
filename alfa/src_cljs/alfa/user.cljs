(ns alfa.user
	(:require [alfa.db :as db]))

(defn exists?
	[]
	(db/get-item "user"))

(defn save!
	[username password]
	(db/set-item! "user"
								{:username username
								 :password password}))

(defn get
	[]
	(db/get-item "user"))


