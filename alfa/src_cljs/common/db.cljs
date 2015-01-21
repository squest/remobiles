(ns common.db
  (:require [cljs.reader :refer [read-string]]))

(defn make-random
  "Generate a random uuid in string format" []
  (let [r (repeatedly 30 (fn [] (.toString (rand-int 16) 16)))]
    (apply str (concat (take 8 r) ["-"]
                       (take 4 (drop 8 r)) ["-4"]
                       (take 3 (drop 12 r)) ["-"]
                       [(.toString
                         (bit-or 0x8 (bit-and 0x3 (rand-int 15))) 16)]
                       (take 3 (drop 15 r)) ["-"]
                       (take 12 (drop 18 r))))))

(defprotocol Database
  "Set of database operations using browser-based database"
  (db-exists? [this]
    "Returns true if the db exist in the browser and false otherwise")
  (all-keys [this]
    "Returns all keys in db")
  (all-items [this]
    "Returns all data in the database")
  (get-item [this k]
    "Get one single item refered by its key [k]")
  (get-items [this ks]
    "Get items listed in vector ks")
  (put-item! [this value]
    "Put an item with value")
  (put-items! [this values]
    "Put a list of items with values in a list/vector")
  (find-item [this query-fn]
    "Find an item that match kval in query")
  (find-items [this query-fn]
    "Find items that match query")
  (remove-item! [this k]
    "Remove an item with a specific k")
  (remove-items! [this ks]
    "Remove items listed in ks")
  (create-db! [this]
    "Create a db with a specific name, will return true if succeed")
  (destroy-db! [this]
    "Destroy the db with all the data in it"))

(defrecord LocalStorage [dbname]
  Database
  (create-db! [this]
    (let [{:keys [dbname]} this]
      (if (nil? (.getItem js/localStorage dbname))
        (do (.setItem js/localStorage dbname [])
            true)
        false)))
  (db-exists? [this]
    (let [{:keys [dbname]} this]
      (if (nil? (.getItem js/localStorage dbname))
        false true)))
  (all-keys [this]
    (if (db-exists? this)
      (let [{:keys [dbname]} this]
        (->> (.getItem js/localStorage dbname)
             read-string))
      false))
  (all-items [this]
    (let [{:keys [dbname]} this]
      (if (db-exists? this)
        (map #(read-string (.getItem js/localStorage %))
             (read-string (.getItem js/localStorage (str dbname))))
        false)))
  (put-item! [this value]
    (if (db-exists? this)
      (let [{:keys [dbname]} this
            dbkey (str dbname "/" (make-random))
            dbvalue value
            old-data (->> (str dbname)
                          (.getItem js/localStorage)
                          (read-string))]
        (do (.setItem js/localStorage dbkey dbvalue)
            (.setItem js/localStorage
                      dbname
                      (str (conj old-data dbkey)))
            {dbkey value}))
      false))
  (put-items! [this values]
    (if (db-exists? this)
      (let [{:keys [dbname]} this
            old-data (->> (str dbname)
                          (.getItem js/localStorage)
                          (read-string))]
        (loop [v values res []]
          (if (empty? v)
            (do (.setItem js/localStorage dbname
                          (str (vec (concat old-data res))))
                res)
            (let [dbkey (str dbname "/" (make-random))
                  value (first v)]
              (do (.setItem js/localStorage dbkey value)
                  (recur (rest v)
                         (conj res dbkey)))))))
      false))
  (get-item [this k]
    (if (db-exists? this)
      (let [{:keys [dbname]} this]
        (if-let [data (.getItem js/localStorage k)]
          (read-string data)
          nil))
      false))
  (get-items [this ks]
    (map #(get-item this %) ks))
  (find-item [this query-fn]
    (if (db-exists? this)
      (let [{:keys [dbname]} this
            avails (->> (.getItem js/localStorage dbname)
                        read-string)]
        (->> (map #(get-item this %) avails)
             (drop-while (complement query-fn))
             first))
      false))
  (find-items [this query-fn]
    (if (db-exists? this)
      (filter query-fn (all-items this))
      false))
  (remove-item! [this k]
    (if (db-exists? this)
      (let [{:keys [dbname]} this
            all-data (read-string (.getItem js/localStorage dbname))]
        (do (->> (remove #{k} all-data)
                 str
                 (.setItem js/localStorage dbname))
            (.removeItem js/localStorage (str k))))
      false))
  (remove-items! [this ks]
    (doseq [item ks]
      (remove-item! this item )))
  (destroy-db! [this]
    (if (db-exists? this)
      (let [{:keys [dbname]} this]
        (do (->> (.getItem js/localStorage dbname)
                 read-string
                 (remove-items! this))
            (.removeItem js/localStorage dbname)))
      false)))

(defrecord Pouch [dbname]
  Database
  (db-exists? [this])
  (create-db! [this]
    "Create a db with a specific name, will return true if succeed")
  (all-keys [this]
    "Returns all keys in db")
  (all-items [this]
    "Returns all data in the database")
  (get-item [this k]
    "Get one single item refered by its key [k]")
  (get-items [this ks]
    "Get items listed in vector ks")
  (put-item! [this value]
    "Put an item with value")
  (put-items! [this values]
    "Put a list of items with values in a list/vector")
  (find-item [this query-fn]
    "Find an item that match kval in query")
  (find-items [this query-fn]
    "Find items that match query")
  (remove-item! [this k]
    "Remove an item with a specific k")
  (remove-items! [this ks]
    "Remove items listed in ks")
  (destroy-db! [this]
    "Destroy the db with all the data in it"))

(defn make-local-storage
  [dbname]
  (LocalStorage. dbname))






