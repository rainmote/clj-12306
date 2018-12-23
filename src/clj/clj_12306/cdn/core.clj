(ns clj-12306.cdn.core
  (:require [clj-12306.cdn.ip :as ip]
            [clj-12306.cdn.chinaz :as chinaz]
            [clj-12306.db.core :as db]
            [clojure.tools.logging :as log]))

(defn get-exist-node [domain]
  (->> (db/get-cdn-node {:domain domain})
       (map :ip ,)
       (into #{} ,)))

(defn get-need-update-node [domain]
  (-> #{}
      (clojure.set/union , (chinaz/get-cdn-node domain))
      (clojure.set/difference , (get-exist-node domain))
      ((partial into []) ,)))

(defn add-cdn-node [domain node]
  (log/info node)
  (-> {:ip (:query node)
       :domain domain
       :country (:country node)
       :country_code (:countryCode node)
       :region (:region node)
       :region_name (:regionName node)
       :city (:city node)
       :isp (:isp node)
       :org (:org node)
       :as_info (:as node)
       :longitude (:lon node)
       :latitude (:lat node)}
      (db/add-cdn-node ,)))

(defn update-cdn-node [domain]
  (log/info "update cdn node, domain:" domain)
  (->> (get-need-update-node domain)
       (ip/query ,)
       (map #(add-cdn-node domain %) ,)
       (count ,)
       (log/info "count " ,)))

(comment
  (remove-exist-node #{})
  )
