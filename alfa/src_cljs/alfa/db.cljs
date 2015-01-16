(ns alfa.db
  (:require [cljs.reader :refer [read-string]]))


(defn get-item
  "Get item from localstorage where k is the key in string,
	the result will be read-string, the content is intended to be
	a valid clojure data" [k]
  (if-let [content (.getItem js/localStorage k)]
    (read-string content)
    nil))

(defn set-item!
  "Set item into the localstorage where k is the key in string,
	and value is a value intended to be stored, the value will be
  stringified" [k value]
  (.setItem js/localStorage k (str value)))

(defn remove-item!!
  "Remove a key k from localstorage"
  [k]
  (.removeItem js/localStorage k))

(defn clear!
  "Clear the existing content in db, this removes all data"
  []
  (.clear js/localStorage))




