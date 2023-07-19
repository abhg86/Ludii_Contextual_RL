import jsonlines as jsl
import numpy as np
import lightgbm as lgb

data = []
with jsl.open('data/raw/combined.jsonl') as f:
    for line in f.iter():
        byte_tuple = ''.join(format(x, 'b') for x in bytearray(str(line), 'utf-8'))
        data += [[int(x) for x in byte_tuple]]

length = max(map(len,data))
X_train = np.array([x+[None]*(length-len(x)) for x in data])
y_train = np.array([1 for i in range(len(data))])

gbm = lgb.LGBMRegressor()
gbm.fit(X_train, y_train)

print(gbm.predict(np.array(X_train[0])))