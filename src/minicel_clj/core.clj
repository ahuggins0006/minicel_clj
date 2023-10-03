(ns core
  (:require [coffi.ffi :as cfn]
            [clojure.tools.cli :as cli]
            [clojure.data.csv :as csv]
            ))


(defn run [& args]
  (let [opts (cli/parse-opts args [["-f" "--file NAME"] ["-h" "--help"]] :in-order true)
        file (get-in opts [:options :file])]
    [opts file]
    (csv/read-csv (slurp file))))

(run "--file" "resources/test.csv")
;; => (["A" "B"] ["1" "2"] ["3" "4"] ["=A1+B1" " =A2+B2"] [""] [""] ["A" "B"] ["1" "2"] ["3" "4"] ["3" "7"])
;; => "A,B\n1,2\n3,4\n=A1+B1, =A2+B2\n\n\nA,B\n1,2\n3,4\n3,7\n"
;; => [{:options {:file "hello.txt"}, :arguments [], :summary "  -f, --file NAME\n  -h, --help", :errors nil} "hello.txt"]

