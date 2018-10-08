(ns clj-12306.ajax
  (:require [ajax.core :as ajax]
            [re-frame.core :as rf]))

(defn local-uri? [{:keys [uri]}]
  (not (re-find #"^\w+?://" uri)))

(defn default-headers [request]
  (if (local-uri? request)
    (-> request
        (update :headers #(merge {"x-csrf-token" js/csrfToken} %)))
    (-> request
        (update :headers #(merge % {"Origin" "null"})))))

(defn load-interceptors! []
  (swap! ajax/default-interceptors
         conj
         (ajax/to-interceptor {:name "default headers"
                               :request default-headers})))

(def http-methods
  {:get ajax/GET
   :post ajax/POST
   :put ajax/PUT
   :delete ajax/DELETE})

(rf/reg-fx
  :http
  (fn [{:keys [method
               url
               success-event
               error-event
               params
               ajax-map]
        :or {error-event [:common/set-error]
             ajax-map {:timeout 5000
                       ;:format (ajax/json-request-format)
                       :response-format (ajax/json-response-format {:keywords? true})}}}]
    ((http-methods method)
      url (merge
            {:params params
             :handler (fn [response]
                        (when success-event
                          (rf/dispatch (conj success-event response))))
             :error-handler (fn [error]
                              (rf/dispatch (conj error-event error)))}
            ajax-map))))



