(ns reminderbot.hawking
  (:require [clojure.core.protocols :as p]
            [clojure.datafy :as d])
  (:import [java.util Date]
           [org.joda.time DateTime]
           [com.zoho.hawking HawkingTimeParser]
           [com.zoho.hawking.datetimeparser.configuration HawkingConfiguration]
           [com.zoho.hawking.language.english.model ParserOutput DateRange]))

(defn- ->instant [^DateTime dt]
  (-> dt .toDate .toInstant))

(extend-protocol p/Datafiable
  DateRange
  (datafy [d]
    {:start      (->instant (.getStart d))
     :end        (->instant (.getEnd d))
     :match-type (.getMatchType d)}))

(extend-protocol p/Datafiable
  ParserOutput
  (datafy [d]
    {:id                 (.getId d)
     :date-range         (d/datafy (.getDateRange d))
     :parser-label       (.getParserLabel d)
     :parser-start-index (.getParserStartIndex d)
     :parser-end-index   (.getParserEndIndex d)
     :text               (.getText d)
     :tz?                (.getIsTimeZonePresent d)
     :exact?             (.getIsExactTimePresent d)
     :tz-offset          (.getTimezoneOffset d)}))

(def parser (HawkingTimeParser.))

(defn parse
  [input]
  (map d/datafy
       (.getParserOutputs (.parse parser input (Date.) (HawkingConfiguration.) "eng"))))
