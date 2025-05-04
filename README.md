# 学生信息管理系统

一个基于 Java 开发的学生信息管理系统，提供完整的学籍管理、成绩管理、用户管理等功能。

## 🌟 功能特点

- **用户管理**
  - 多角色支持（管理员、教师、学生）
  - 安全的登录系统
  - 用户信息管理

- **学生管理**
  - 学生基本信息管理
  - 学籍信息维护
  - 学生档案查询

- **成绩管理**
  - 成绩录入与修改
  - 成绩查询与统计
  - 成绩分析报表

- **系统特点**
  - 简洁直观的用户界面
  - 安全的数据存储
  - 高效的数据处理

## 🛠️ 技术栈

- **后端**
  - Java 8+
  - MySQL 数据库
  - JDBC 数据库连接

- **前端**
  - Java Swing
  - JavaFX (可选)

## 📦 安装说明

1. **环境要求**
   - Java JDK 8+
   - MySQL 5.7+
   - Maven 3.6+

2. **安装步骤**
   ```bash
   # 克隆仓库
   git clone https://github.com/Floralsei/student-information-management-system.git

   # 进入项目目录
   cd student-information-management-system

   # 使用 Maven 安装依赖
   mvn clean install
   ```

3. **数据库初始化**
   - 创建 MySQL 数据库
   - 导入数据库脚本（位于 `src/main/resources/database.sql`）
   - 配置数据库连接（修改 `src/main/resources/db.properties`）

## 🚀 使用方法

1. **启动系统**
   ```bash
   # 编译项目
   mvn compile

   # 运行项目
   mvn exec:java -Dexec.mainClass="com.studentinfo.Main"
   ```

2. **登录系统**
   - 使用管理员账号登录进行系统管理
   - 使用教师账号登录进行成绩管理
   - 使用学生账号登录查看个人信息和成绩

3. **主要功能**
   - 在菜单栏选择相应功能模块
   - 按照界面提示进行操作
   - 数据会自动保存到数据库

## 📝 开发说明

- 项目采用 MVC 架构设计
- 数据库操作封装在 `com.studentinfo.database` 包
- 用户界面代码位于 `com.studentinfo.ui` 包
- 主要业务逻辑在 `com.studentinfo.service` 包
- 实体类在 `com.studentinfo.model` 包

## 🤝 贡献指南

欢迎提交 Issue 和 Pull Request 来帮助改进项目。

1. Fork 本仓库
2. 创建你的特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交你的更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 打开一个 Pull Request

## 📄 许可证

本项目采用 MIT 许可证 - 查看 [LICENSE](LICENSE) 文件了解详情

## 📞 联系方式

如有任何问题或建议，请通过以下方式联系：

- 提交 Issue

## 🙏 致谢

感谢所有为本项目做出贡献的开发者！
