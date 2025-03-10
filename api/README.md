
# 🚀 Scala Play Framework API

## 📌 技術スタック
- **Scala** (Play Framework)
- **MySQL+Redis** (DB)

### 📦 DB初期化
```bash
CREATE DATABASE app;

CREATE TABLE user (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    role INT NOT NULL COMMENT 'User role, 1 for Admin, 2 for Operator',
    status INT NOT NULL DEFAULT 1 COMMENT 'Account status, 1 for active, 2 for inactive',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

// get password(xxxxxxxxx)
sbt "runMain utils.PasswordHasher"

INSERT INTO user (username, password, role) VALUES ('admin', 'xxxxxxx', 1);


# CREATE TABLE categories (
#     id INT AUTO_INCREMENT PRIMARY KEY,
#     name VARCHAR(255) NOT NULL,
#     created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
#     updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
# )

CREATE TABLE volunteer (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    phone_number VARCHAR(255) NOT NULL,
    gender INT NOT NULL COMMENT 'Gender, 1 for male, 2 for female',
    status INT NOT NULL DEFAULT 1 COMMENT 'Account status, 1 for active, 2 for inactive',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE issue (
    issue_id INT PRIMARY KEY,
    wiki_content TEXT,
    volunteer_ids VARCHAR(256),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);


```


### 🛠️ ローカルで起動

```bash
sbt clean
sbt compile
sbt run
```

### API
```bash
# login API
POST    /login

# users API
POST    /user
GET     /user
PUT     /user/:id

# volunteer API（バックログでユーザーを作成する権限がないため、MySQLを使用）
POST    /volunteer           controllers.VolunteerController.addVolunteer
GET     /volunteer           controllers.VolunteerController.getVolunteers
PUT     /volunteer/:id       controllers.VolunteerController.updateVolunteer(id: Int)

# backlog API
POST    /backlog/token    controllers.BacklogAuthController.getAccessToken

GET     /issueType           controllers.IssueTypeController.getIssueTypes
POST    /issueType           controllers.IssueTypeController.addIssueType
PUT     /issueType/:id       controllers.IssueTypeController.updateIssueType(id: Int)

# API（バックログでwikiを作成する権限がないため、MySQLを使用）
GET     /issue           controllers.IssueController.getIssues
POST    /issue           controllers.IssueController.addIssue
PUT     /issue/:id       controllers.IssueController.updateIssue(id: Int)

```

### memo
```bash
docker run --name  my-redis --network host -d redis
docker exec -it my-mysql mysql -u root -p
```

不足している機能点：

1. パラメータの検証が行われていません。 
2. Backlog APIの権限問題のため、一部の削除機能が実装されていません。 
3. PostmanでBacklogトークンの更新APIを呼び出すとエラーが発生するため、現在は有効時間を1時間に設定しており、システムの使用に影響はありません。 
4. WikiにAPIを呼び出す権限がないため、暫定的にwikiContentをMySQLに保存しています。 
5. UIの一部が最適化されておらず、例えばWikiは活動ログとして使用される予定で、多くのテキストが入力される可能性があります。
6. いくつかの定数は環境変数に抽出する必要がある