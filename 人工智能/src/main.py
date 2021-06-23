import pandas as pd
from boto3.session import Session
from sklearn.preprocessing import LabelEncoder
from sklearn.model_selection import train_test_split
from sklearn.preprocessing import StandardScaler
from sklearn.linear_model import LinearRegression
from sklearn.metrics import r2_score

if __name__ == '__main__':
    # 读取数据集
    df = pd.read_csv("train.csv")
    X_test = pd.read_csv("test.csv")

    # 数据值转化
    for c in df.columns:
        if df[c].dtype == 'object':
            lbl = LabelEncoder()
            lbl.fit(list(df[c].values))
            df[c] = lbl.transform(df[c].values)

    for c in X_test.columns:
        if X_test[c].dtype == 'object':
            lbl = LabelEncoder()
            lbl.fit(list(X_test[c].values))
            X_test[c] = lbl.transform(X_test[c].values)

    # 切割训练集和验证集
    X = df.drop(['charges'], axis=1)
    y = df['charges']
    X_train, X_valid, y_train, y_valid = train_test_split(X, y, test_size=0.3, random_state=59)

    # 数据归一化
    sc = StandardScaler()
    X_train = sc.fit_transform(X_train)
    X_valid = sc.fit_transform(X_valid)
    X_test = sc.fit_transform(X_test)

    # 线性回归模型训练
    lr = LinearRegression()
    lr.fit(X_train, y_train)

    # 线性回归模型评估
    y_pred = lr.predict(X_valid)
    accuracy = round(r2_score(y_valid, y_pred), 1) * 100
    print('验证集准确率: {:.0f} %'.format(accuracy))

    # 线性回归模型预测
    y_test = lr.predict(X_test)
    result = pd.DataFrame({'charges': y_test})
    result.to_csv("result.csv", index=False)

    # 将结果传入S3
    session = Session(aws_access_key_id='AF09D809E3ECBAC11F8D',
                      aws_secret_access_key='Wzg2MjU3Q0QxQzIyQTM5MDUxMTk4Njc2NkNDNzU4')
    s3_client = session.client('s3', endpoint_url='http://scuts3.depts.bingosoft.net:29997')
    s3_client.put_object(Bucket='lab2559', Key='result.csv', Body=open('insurance.csv', 'rb').read())
