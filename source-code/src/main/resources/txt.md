# 1. 生成私钥库
# validity：私钥的有效期（天）
# alias：私钥别称
# keystore：私钥库文件名称（生成在当前目录）
# storepass：私钥库密码（获取 keystore 信息所需的密码，密钥库口令）
# keypass：别名条目的密码(密钥口令)

keytool -genkeypair -keyalg DSA -keysize 2048 -validity 3650 -alias "privateKey" -keystore "privateKeys.keystore"  -storetype JKS -storepass "cxyj123456" -dname "CN=localhost, OU=localhost, O=localhost, L=SH, ST=SH, C=CN"

# 2. 把私钥库内的公钥导出到一个文件当中
# alias：私钥别称
# keystore：私钥库的名称（在当前目录查找）
# storepass：私钥库的密码
# file：证书名称

keytool -exportcert -alias "privateKey" -keystore "privateKeys.keystore" -storepass "cxyj123456" -file "certfile.cer"

# 3.再把这个证书文件导入到公钥库，certfile.cer 没用了可以删掉了
# alias：公钥名称
# file：证书名称
# keystore：公钥文件名称
# storepass：公钥库密码

keytool -import -alias "publicCert" -file "certfile.cer" -keystore "publicCerts.keystore" -storepass "cxyj123456"

