; Written by TheBusby <thebusby@gmail.com>
; Licensed under the EPL
(ns lazybot.plugins.jdict
  (:use [lazybot registry [utilities :only [bold-str]]]
        [clojure.data.json :only [read-json]]
        [clojure.core.incubator :only [-?>>]])
  (:require [clj-http.client :as http]))

(defn japanese-text? [s]
  (some #{"KATAKANA" "HIRAGANA" "CJK_UNIFIED_IDEOGRAPHS"}
        (map #(-> %
                  (. charValue)
                  (java.lang.Character$UnicodeBlock/of)
                  (. toString))
             s)))

(def jdic-url "http://www.csse.monash.edu.au/~jwb/cgi-bin/wwwjdic.cgi?1ZU")

(defn get-wwwjdic-url [word]
  (let [encoded-word (java.net.URLEncoder/encode word "UTF8")
        lookup-type  (if (japanese-text? word) "J" "E")]
    (str jdic-url lookup-type encoded-word)))

(defn search-wwwjdic [argstr]
  (->> argstr
     get-wwwjdic-url
     http/get
     :body
     (re-find #"(?s)<pre>(.+)</pre>")
     second
     (re-seq #"(.+)\n")
     (map second)
     (take 3)))

(defplugin
  (:cmd
   "Searches Jim Breen's WWWJDIC for J-E, E-J translation"
   #{"jdict"}
   (fn [{:keys [args] :as com-m}]
     (let [argstr (clojure.string/join " " args)]
       (if-not (seq (clojure.string/trim argstr))
         (str "No search term!")
         (doseq [answer (search-wwwjdic argstr)]
           (send-message com-m (str (bold-str argstr) " |= " answer))))))))