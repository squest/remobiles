(ns alfa.util)

(def ^:private anim-duration 1200)

(defn selid
  "Document getElementById shortened"
  [id]
  (.getElementById js/document id))

(defn animate
  "Animate the content when popped up, to be applied when performing
  any transition, elmt is the id of a certain html element"
  [elmt]
  (.fadeIn js/Jacked (selid elmt)
           (clj->js {:duration anim-duration})))


