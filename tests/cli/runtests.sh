#!/bin/bash

#run make in rabit/test to generate librabit_mock
#update config.mk and build xgboost using mock
export DMLC_SUBMIT_CLUSTER=local

submit="python ../../dmlc-core/tracker/dmlc-submit"
# build xgboost with librabit mock
# define max worker retry with dmlc-core local num atempt
# instrument worker failure with mock=xxxx
# check if host recovered from expectected iteration
echo "====== 1. Fault recovery distributed test with native ======"
$submit --cluster=local --num-workers=10 --local-num-attempt=11 ~/xgboost/xgboost machine.conf mock=0,10,1,0 mock=1,10,1,0 mock=1,10,1,1 mock=0,10,1,0 mock=4,10,1,0 mock=9,10,1,0 mock=8,10,2,0 mock=4,10,3,0