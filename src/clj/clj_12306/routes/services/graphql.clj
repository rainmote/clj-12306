(ns clj-12306.routes.services.graphql
  (:require [com.walmartlabs.lacinia.util :refer [attach-resolvers]]
            [com.walmartlabs.lacinia.schema :as schema]
            [com.walmartlabs.lacinia :as lacinia]
            [clojure.data.json :as json]
            [clojure.edn :as edn]
            [clojure.java.io :as io]
            [ring.util.http-response :refer :all]
            [mount.core :refer [defstate]]
            [clj-12306.db.core :as db]
            [clj-12306.http.client :as client]))

(defn get-hero [context args value]
  (let [data [{:id          1000
               :name        "Luke"
               :home_planet "Tatooine"
               :appears_in  ["NEWHOPE" "EMPIRE" "JEDI"]}
              {:id          2000
               :name        "Lando Calrissian"
               :home_planet "Socorro"
               :appears_in  ["EMPIRE" "JEDI"]}]]
    (first data)))

(defn get-ipinfo-construct-request [ip]
  (-> {}
      (assoc , :query ip)
      (assoc , :lang "zh-CN")))

(defn get-ipinfo [context args value]
  (->> args
       :iplist
       (map get-ipinfo-construct-request ,)
       (partition-all 100 ,)
       (map #(client/batch-query-ip-info %) ,)
       (flatten ,)))

(def api-fn
  {:add-tag    db/add-tag
   :delete-tag db/delete-tag
   :get-tag    db/get-tag
   :get-hero   get-hero
   :get-droid  (constantly {})
   :get-ipinfo get-ipinfo})

(defstate compiled-schema
          :start
          (-> "graphql/schema.edn"
              io/resource
              slurp
              edn/read-string
              (attach-resolvers api-fn)
              schema/compile))

(defn format-params [query]
  (let [parsed (json/read-str query)]                       ;;-> placeholder - need to ensure query meets graphql syntax
    (str "query { hero(id: \"1000\") { name appears_in }}")))

(defn execute-request [query]
  (let [vars    nil
        context nil]
    (-> (lacinia/execute compiled-schema query vars context)
        (json/write-str))))
