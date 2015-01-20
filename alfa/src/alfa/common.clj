(ns alfa.common)

(defprotocol ICode
	(reverse-print [seqable])
	(ngawur [seqable]))

(extend-type String
	ICode
	(reverse-print [some-string]
		(apply str (reverse some-string)))
	(ngawur [this]
		(str this " what???")))

(extend-type clojure.lang.Seqable
	ICode
	(reverse-print [seqable]
		(->> (str seqable)
				 reverse
				 (apply str)))
	(ngawur [seqable]
		(conj seqable " whaat?? ")))

(defrecord Person [first-name last-name]
	ICode
	(reverse-print [this]
		(str (:last-name this) (:first-name this)))
	(ngawur [this]
		(str (:first-name this) " whoo??")))

(deftype Animal [species]
	ICode
	(reverse-print [this]
		(apply str (reverse (.species this)))))
