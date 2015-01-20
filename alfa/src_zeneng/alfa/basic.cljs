(ns alfa.basic
 (:require [ajax.core :refer [ajax-request
                              edn-request-format
                              json-response-format
                              edn-response-format]]))

(ajax-request
 {:uri "/woli"
  :method :post
  :params {:name "well" :mati-lu "Well mati dehh"}
  :format (edn-request-format)
  :response-format (edn-response-format)
  :handler (fn [[status data]]
               (do (js/alert status)
                   (.log js/console data)
                   (js/alert (:name data))))})

