(ns clj-12306.query
  (:require [clj-http.client :as client]
            [clj-http.cookies]
            [clj-12306.http.useragent :as ua]
            [clj-12306.config :refer [env]]
            [clojure.core.async :as async]))

(def ^:dynamic *host-cookie* (atom {}))

(def http-proxy
  {:proxy-host "127.0.0.1"
   :proxy-port 8080})

(def urls
  {:init "https://%s/otn/leftTicket/init"
   :query "https://%s/otn/leftTicket/query?leftTicketDTO.train_date=%s&leftTicketDTO.from_station=%s&leftTicketDTO.to_station=%s&purpose_codes=ADULT"})

(defn get-init-url [host]
  (format (:init urls) host))

(defn get-query-url [host date from to]
  (format (:query urls) host date from to))

(def default-headers
  {"Accept" "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8"
   "Accept-Encoding" "gzip, deflate, br"
   "Accept-Language" "zh-CN,zh;q=0.9"
   "Host" "kyfw.12306.cn"})

(defn construct-headers [header]
  (-> default-headers
      (assoc , "User-Agent" (rand-nth ua/user-agent-list))
      (merge , (or header {}))))

(defn construct-params
  [{:keys [form-params headers]
    :or {form-params {}
         headers {}}
    :as param}]
  (-> {:conn-timeout 2000
       :socket-timeout 2000
       :insecure true ;; for https
       :throw-exceptions false
       :ignore-unknown-host? true}
      (merge , http-proxy)
      (merge , param)
      (assoc , :form-params form-params)
      (assoc , :headers (construct-headers headers))))

(defn create-cookie [host]
  (let [cs (clj-http.cookies/cookie-store)]
    (->> {:cookie-store cs}
         (construct-params ,)
         (client/get (get-init-url host) ,)
         (#(-> %
               :status
               (= , 200)
               (when , (swap! *host-cookie* assoc-in [host :cookie] cs))) ,))
    (identity cs)))

(defn get-cookie [host]
  (let [m (get @*host-cookie* host)]
    (cond
      (-> (:ttl m)
          (or , 0)
          (pos? ,)
          (if , (some? (:cookie m)) false))
      (do
        (swap! *host-cookie* update-in [host :ttl] dec)
        (get m :cookie))

      :else
      (do
        (->> (or (env :session-ttl) 1000)
             (swap! *host-cookie* assoc-in [host :ttl] ,))
        (create-cookie host)))))

(def query-resp-key
  {:secret 0
   :note 1
   :train-no 2
   :train-code 3
   :start-station 4
   :end-station 5
   :from-station 6
   :to-station 7
   :start-time 8
   :arrive-time 9
   :duration 10
   :buy? 11
   :yp-info 12
   :date 13
   :seat-feature 14
   :location-code 15
   :from-station-no 16
   :to-station-no 17
   :support-card? 18
   :train-flag 19
   :rw-num 23
   :rz-num 24
   :wz-num 26
   :yw-num 28
   :yz-num 29
   :edz-num 30
   :ydz-num 31
   :swz-num 32
   :yp-ex 34
   :seat-types 35
   :exchange-train-flag 36})

(defn convert-train-info
  [info]
  (->> query-resp-key
       (map (fn [[k v]] (vector k (nth info v))) ,)
       (into {} ,)))

(defn single-request
  [{:keys [host channel date from to]}]
  ;(async/thread
    (->> {:cookie-store (get-cookie host)
          :headers {"Referer" "https://kyfw.12306.cn/otn/leftTicket/init"}
          :as :json}
         (construct-params ,)
         (client/get (get-query-url host date from to) ,)
         :body
         :data
         :result
         (map #(-> %
                   (clojure.string/split , #"|")
                   ) ,)
        ))

(defn query-left-ticket
  [{:keys [hosts date from to timeout]
    :or {timeout 20000}}]
  (let [host-channel (map #(vector % async/chan) hosts)
        channels (conj (mapv second host-channel)
                       (async/timeout timeout))]
    "123"))
