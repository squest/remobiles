(ns alfa.common)

(defprotocol Database
  (get-item [this k])
  (get-items [this ks])
  (put-item [this k value])
  (put-items [this kvals])
  (find-item [this query])
  (find-items [this query])
  (remove-item! [this k])
  (remove-items! [this ks])
  (create-db! [this name])
  (destroy-db! [this name]))

(defprotocol Shape
  "Set of operations for geometric objects"
  (area [this]
    "Returns the area of a specific shape")
  (perimeter [this]
    "Returns the perimeter of a shape"))

(defrecord Rectangle [length width]
  Shape
  (area [this]
    (let [{:keys [length width]} this]
      (* length width)))
  (perimeter [this]
    (let [{:keys [length width]} this]
      (* 2 (+ length width)))))

