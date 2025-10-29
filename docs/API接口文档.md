# 东山社区平台 API 接口文档

## 文档说明

本文档详细描述了开源社区平台后端系统的所有HTTP接口，包括请求方法、参数说明、返回数据结构等信息。

### 通用说明

**基础路径**: `/api`

**通用响应格式** (`ApiResponse<T>`):
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {}
}
```

**响应状态码**:
- `200`: 操作成功
- `400`: 请求参数错误
- `404`: 资源不存在
- `500`: 服务器内部错误

---

## 目录

1. [认证模块](#1-认证模块)
2. [用户模块](#2-用户模块)
3. [项目模块](#3-项目模块)
4. [任务模块](#4-任务模块)
5. [活动模块](#5-活动模块)
6. [文档模块](#6-文档模块)
7. [镜像模块](#7-镜像模块)
8. [首页模块](#8-首页模块)
9. [关于模块](#9-关于模块)
10. [验证码模块](#10-验证码模块)
11. [管理员模块](#11-管理员模块)

---

## 1. 认证模块

### 1.1 用户登录

**接口地址**: `POST /api/auth/login`

**接口功能**: 用户通过用户名和密码进行登录认证，成功后返回JWT令牌

**请求参数** (Body - JSON):
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| username | String | 是 | 用户名 |
| password | String | 是 | 密码 |

**请求示例**:
```json
{
  "username": "zhangsan",
  "password": "123456"
}
```

**返回数据** (`ApiResponse<JwtResponse>`):
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "username": "zhangsan",
    "role": "USER"
  }
}
```

**返回字段说明**:
| 字段 | 类型 | 说明 |
|------|------|------|
| token | String | JWT访问令牌，用于后续请求认证 |
| username | String | 用户名 |
| role | String | 用户角色（USER/ADMIN） |

---

### 1.2 用户注册

**接口地址**: `POST /api/auth/register`

**接口功能**: 新用户注册账号，需要提供邮箱验证码

**请求参数** (Body - JSON):
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| username | String | 是 | 用户名 |
| password | String | 是 | 密码 |
| email | String | 是 | 主邮箱 |
| verificationCode | String | 是 | 邮箱验证码 |
| fullname | String | 否 | 真实姓名 |
| email2 | String | 否 | 备用邮箱 |
| phone | String | 否 | 手机号 |
| company | String | 否 | 单位/公司 |
| address | String | 否 | 地址 |

**请求示例**:
```json
{
  "username": "zhangsan",
  "password": "123456",
  "email": "zhangsan@example.com",
  "verificationCode": "123456",
  "fullname": "张三",
  "phone": "13800138000",
  "company": "某某公司",
  "address": "北京市"
}
```

**返回数据** (`ApiResponse<User>`):
```json
{
  "code": 200,
  "message": "注册成功",
  "data": {
    "id": 1,
    "username": "zhangsan",
    "email": "zhangsan@example.com",
    "fullname": "张三",
    "role": "USER",
    "enabled": true,
    "hasSignedPdf": false,
    "createTime": "2025-01-01 12:00:00"
  }
}
```

**错误返回**:
- `400`: 用户名已存在 / 验证码不正确 / 验证码已过期

---

### 1.3 发送邮箱验证码

**接口地址**: `GET /api/public/sendEmail/{email}`

**接口功能**: 向指定邮箱发送注册验证码，验证码有效期5分钟

**请求参数** (Path):
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| email | String | 是 | 邮箱地址 |

**请求示例**:
```
GET /api/public/sendEmail/zhangsan@example.com
```

**返回数据**:
```json
{
  "code": 200,
  "message": "验证码已开始发送，请查收邮件（若5分钟内未收到，可重新获取）",
  "data": null
}
```

---

### 1.4 忘记密码 - 发送重置邮件

**接口地址**: `POST /api/auth/forgot-password`

**接口功能**: 用户忘记密码时，通过邮箱和图形验证码获取密码重置链接

**请求参数** (Body - JSON):
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| forgotEmail | String | 是 | 注册邮箱 |
| captchaCode | String | 是 | 图形验证码 |

**请求示例**:
```json
{
  "forgotEmail": "zhangsan@example.com",
  "captchaCode": "ABCD"
}
```

**返回数据**:
```json
{
  "code": 200,
  "message": "密码重置邮件已发送，请注意查收",
  "data": null
}
```

---

### 1.5 重置密码

**接口地址**: `POST /api/auth/reset-password?token={token}`

**接口功能**: 通过邮件中的重置令牌设置新密码

**请求参数**:
- **Query参数**:
  | 参数名 | 类型 | 必填 | 说明 |
  |--------|------|------|------|
  | token | String | 是 | 密码重置JWT令牌 |

- **Body参数** (JSON):
  | 参数名 | 类型 | 必填 | 说明 |
  |--------|------|------|------|
  | newPassword | String | 是 | 新密码（最少6位） |
  | confirmPassword | String | 是 | 确认密码 |

**请求示例**:
```json
{
  "newPassword": "newpass123",
  "confirmPassword": "newpass123"
}
```

**返回数据**:
```json
{
  "code": 200,
  "message": "密码重置成功，请使用新密码登录",
  "data": null
}
```

---

## 2. 用户模块

### 2.1 获取个人信息

**接口地址**: `POST /api/personInfo`

**接口功能**: 获取指定用户的个人详细信息

**鉴权**: 需要登录

**请求参数** (Body - JSON):
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| username | String | 是 | 用户名 |

**请求示例**:
```json
{
  "username": "zhangsan"
}
```

**返回数据** (`ApiResponse<User>`):
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "id": 1,
    "username": "zhangsan",
    "email": "zhangsan@example.com",
    "fullname": "张三",
    "email2": "backup@example.com",
    "phone": "13800138000",
    "company": "某某公司",
    "address": "北京市海淀区",
    "bankCardNumber": "6222021234567890",
    "giteeName": "zhangsan_gitee",
    "role": "USER",
    "enabled": true,
    "hasSignedPdf": true,
    "createTime": "2025-01-01 12:00:00",
    "updateTime": "2025-01-15 10:30:00"
  }
}
```

**User字段说明**:
| 字段 | 类型 | 说明 |
|------|------|------|
| id | Long | 用户ID |
| username | String | 用户名 |
| email | String | 主邮箱 |
| fullname | String | 真实姓名 |
| email2 | String | 备用邮箱 |
| phone | String | 手机号 |
| company | String | 单位/公司 |
| address | String | 地址 |
| bankCardNumber | String | 银行卡号 |
| giteeName | String | Gitee用户名 |
| role | String | 角色（USER/ADMIN） |
| enabled | Boolean | 账号是否启用 |
| hasSignedPdf | Boolean | 是否已签署实习协议 |
| createTime | String | 创建时间 |
| updateTime | String | 更新时间 |

---

### 2.2 更新个人信息

**接口地址**: `PUT /api/updatePersonInfo`

**接口功能**: 更新用户的个人信息

**鉴权**: 需要登录

**请求参数** (Body - JSON):
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| username | String | 是 | 用户名（用于定位用户） |
| fullname | String | 否 | 真实姓名 |
| email2 | String | 否 | 备用邮箱 |
| phone | String | 是 | 手机号（11位有效手机号） |
| company | String | 否 | 单位/公司（最大50字符） |
| address | String | 否 | 地址（最大100字符） |
| bankCardNumber | String | 否 | 银行卡号（13-19位数字） |
| giteeName | String | 否 | Gitee用户名 |

**请求示例**:
```json
{
  "username": "zhangsan",
  "fullname": "张三",
  "phone": "13800138000",
  "company": "新公司",
  "address": "上海市浦东新区",
  "bankCardNumber": "6222021234567890",
  "giteeName": "zhangsan_gitee"
}
```

**返回数据**:
```json
{
  "code": 200,
  "message": "个人信息更新成功",
  "data": null
}
```

---

### 2.3 获取协议文案

**接口地址**: `GET /api/public/getPdfCW?area={area}`

**接口功能**: 获取指定区域的协议文案内容（如实习协议等）

**请求参数** (Query):
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| area | String | 是 | 文案区域标识 |

**请求示例**:
```
GET /api/public/getPdfCW?area=internship-agreement
```

**返回数据** (直接返回CopyWriting对象):
```json
{
  "id": 1,
  "area": "internship-agreement",
  "title": "实习协议",
  "copyWritingText": "协议内容文本...",
  "link": "https://example.com/agreement.pdf",
  "note": "备注信息"
}
```

**CopyWriting字段说明**:
| 字段 | 类型 | 说明 |
|------|------|------|
| id | Long | 文案ID |
| area | String | 区域标识 |
| title | String | 标题 |
| copyWritingText | String | 文案文本内容 |
| link | String | 相关链接 |
| note | String | 备注 |

---

## 3. 项目模块

### 3.1 获取项目列表（分页）

**接口地址**: `GET /api/projects?pageNum={pageNum}&pageSize={pageSize}`

**接口功能**: 分页获取开源项目列表

**请求参数** (Query):
| 参数名 | 类型 | 必填 | 默认值 | 说明 |
|--------|------|------|--------|------|
| pageNum | Integer | 否 | 1 | 页码（从1开始） |
| pageSize | Integer | 否 | 10 | 每页显示数量 |

**请求示例**:
```
GET /api/projects?pageNum=1&pageSize=10
```

**返回数据** (`ProjectListVO`):
```json
{
  "pageNum": 1,
  "pageSize": 10,
  "total": 25,
  "pages": 3,
  "projectList": [
    {
      "id": 1,
      "name": "项目名称",
      "description": "项目简短描述",
      "createTime": "2025-01-01 10:00:00",
      "gitRepo": "https://github.com/example/project",
      "projectIntro": "项目详细介绍",
      "moduleDisplay": {
        "name": true,
        "tags": true,
        "gitRepo": true,
        "projectIntro": true,
        "projectDisplay": true,
        "learningMaterial": true
      }
    }
  ]
}
```

**ProjectListVO字段说明**:
| 字段 | 类型 | 说明 |
|------|------|------|
| pageNum | Integer | 当前页码 |
| pageSize | Integer | 每页数量 |
| total | Long | 总记录数 |
| pages | Integer | 总页数 |
| projectList | List&lt;Project&gt; | 项目列表 |

**Project字段说明**:
| 字段 | 类型 | 说明 |
|------|------|------|
| id | Long | 项目ID |
| name | String | 项目名称 |
| description | String | 项目简短描述 |
| createTime | String | 创建时间 |
| gitRepo | String | Git仓库地址 |
| projectIntro | String | 项目详细介绍 |
| moduleDisplay | JsonNode | 模块显示配置（各模块是否显示） |

---

### 3.2 获取项目详情

**接口地址**: `GET /api/projects/{id}`

**接口功能**: 获取指定项目的详细信息，包括项目展示和学习资料

**请求参数** (Path):
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | Long | 是 | 项目ID |

**请求示例**:
```
GET /api/projects/1
```

**返回数据** (`ProjectDetailDTO`):
```json
{
  "id": 1,
  "name": "开源项目名称",
  "description": "项目简短描述",
  "createTime": "2025-01-01 10:00:00",
  "gitRepo": "https://github.com/example/project",
  "projectIntro": "项目详细介绍内容...",
  "moduleDisplay": {
    "name": true,
    "tags": true,
    "gitRepo": true,
    "projectIntro": true,
    "projectDisplay": true,
    "learningMaterial": true
  },
  "projectDisplays": [
    {
      "id": 10,
      "area": "PROJECT_DISPLAY",
      "title": "项目截图1",
      "link": "https://example.com/image1.png",
      "note": "首页截图"
    }
  ],
  "learningMaterials": [
    {
      "id": 20,
      "area": "LEARNING_MATERIAL",
      "title": "开发文档",
      "link": "https://example.com/docs.pdf",
      "note": "项目开发指南"
    }
  ]
}
```

**ProjectDetailDTO字段说明**:
| 字段 | 类型 | 说明 |
|------|------|------|
| id | Long | 项目ID |
| name | String | 项目名称 |
| description | String | 项目简短描述 |
| createTime | String | 创建时间 |
| gitRepo | String | Git仓库地址 |
| projectIntro | String | 项目详细介绍 |
| moduleDisplay | JsonNode | 模块显示配置 |
| projectDisplays | List&lt;CopyWriting&gt; | 项目展示内容列表（图片、链接等） |
| learningMaterials | List&lt;CopyWriting&gt; | 学习资料列表 |

---

### 3.3 获取项目标签

**接口地址**: `GET /api/projects/{id}/tags`

**接口功能**: 获取指定项目关联的所有标签

**请求参数** (Path):
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | Long | 是 | 项目ID |

**请求示例**:
```
GET /api/projects/1/tags
```

**返回数据**:
```json
[
  {
    "id": 1,
    "name": "Java"
  },
  {
    "id": 2,
    "name": "Spring Boot"
  },
  {
    "id": 3,
    "name": "后端"
  }
]
```

---

### 3.4 获取所有标签

**接口地址**: `GET /api/projects/tags`

**接口功能**: 获取系统中所有项目标签

**返回数据**:
```json
[
  {
    "id": 1,
    "name": "Java"
  },
  {
    "id": 2,
    "name": "Spring Boot"
  },
  {
    "id": 3,
    "name": "Python"
  }
]
```

**Tag字段说明**:
| 字段 | 类型 | 说明 |
|------|------|------|
| id | Long | 标签ID |
| name | String | 标签名称 |

---

### 3.5 创建项目（管理员）

**接口地址**: `POST /api/admin/project/createProject`

**接口功能**: 创建新的开源项目

**鉴权**: 需要管理员权限

**请求参数** (Body - JSON):
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| name | String | 是 | 项目名称 |
| description | String | 是 | 项目简短描述 |
| gitRepo | String | 否 | Git仓库地址 |
| projectIntro | String | 否 | 项目详细介绍 |
| moduleDisplay | JsonNode | 否 | 模块显示配置 |

**请求示例**:
```json
{
  "name": "新项目",
  "description": "项目简短描述",
  "gitRepo": "https://github.com/example/new-project",
  "projectIntro": "这是一个新的开源项目",
  "moduleDisplay": {
    "name": true,
    "tags": true,
    "gitRepo": true
  }
}
```

**返回数据**:
```json
{
  "id": 10,
  "name": "新项目",
  "description": "项目简短描述",
  "createTime": "2025-01-20 15:30:00",
  "gitRepo": "https://github.com/example/new-project",
  "projectIntro": "这是一个新的开源项目",
  "moduleDisplay": {
    "name": true,
    "tags": true,
    "gitRepo": true
  }
}
```

**状态码**:
- `201`: 创建成功
- `500`: 创建失败

---

### 3.6 添加项目标签（管理员）

**接口地址**: `POST /api/admin/project/addProjectTag`

**接口功能**: 为项目添加标签关联

**鉴权**: 需要管理员权限

**请求参数** (Body - JSON):
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| projectId | Long | 是 | 项目ID |
| tagIds | List&lt;Long&gt; | 是 | 标签ID列表 |

**请求示例**:
```json
{
  "projectId": 1,
  "tagIds": [1, 2, 3]
}
```

**状态码**:
- `201`: 添加成功
- `400`: 添加失败
- `500`: 服务器错误

---

### 3.7 添加新标签（管理员）

**接口地址**: `POST /api/admin/project/addTag`

**接口功能**: 创建新的项目标签

**鉴权**: 需要管理员权限

**请求参数** (Body - JSON):
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| name | String | 是 | 标签名称 |

**请求示例**:
```json
{
  "name": "微服务"
}
```

**状态码**:
- `201`: 创建成功
- `400`: 标签名称为空或创建失败
- `500`: 服务器错误

---

### 3.8 添加项目文案（管理员）

**接口地址**: `POST /api/admin/project/addProjectCW`

**接口功能**: 批量添加项目展示图片或学习资料

**鉴权**: 需要管理员权限

**请求参数** (Body - JSON):
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| projectId | Long | 是 | 项目ID |
| cwType | String | 是 | 文案类型（PROJECT_DISPLAY/LEARNING_MATERIAL） |
| cwList | List&lt;CopyWriting&gt; | 是 | 文案列表 |

**请求示例**:
```json
{
  "projectId": 1,
  "cwType": "PROJECT_DISPLAY",
  "cwList": [
    {
      "title": "项目首页截图",
      "link": "https://example.com/screenshot1.png",
      "note": "首页展示"
    },
    {
      "title": "项目架构图",
      "link": "https://example.com/architecture.png",
      "note": "系统架构"
    }
  ]
}
```

**状态码**:
- `201`: 添加成功
- `400`: 参数错误或添加失败
- `500`: 服务器错误

---

### 3.9 更新项目基础信息（管理员）

**接口地址**: `PUT /api/admin/project/updateProject`

**接口功能**: 更新项目的基础信息

**鉴权**: 需要管理员权限

**请求参数** (Body - JSON):
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| projectId | Long | 是 | 项目ID |
| name | String | 否 | 项目名称 |
| description | String | 否 | 项目描述 |
| gitRepo | String | 否 | Git仓库地址 |
| projectIntro | String | 否 | 项目详细介绍 |

**请求示例**:
```json
{
  "projectId": 1,
  "name": "更新后的项目名称",
  "description": "更新后的描述",
  "gitRepo": "https://github.com/example/updated-project"
}
```

**返回数据**: 返回更新后的Project对象

**状态码**:
- `200`: 更新成功
- `400`: 项目ID无效
- `404`: 项目不存在
- `500`: 服务器错误

---

### 3.10 更新项目标签（管理员）

**接口地址**: `PUT /api/admin/project/updateProjectTags`

**接口功能**: 全量更新项目的标签关联（先删除原有，再新增）

**鉴权**: 需要管理员权限

**请求参数** (Body - JSON):
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| projectId | Long | 是 | 项目ID |
| tagIds | List&lt;Long&gt; | 是 | 新的标签ID列表 |

**请求示例**:
```json
{
  "projectId": 1,
  "tagIds": [1, 3, 5, 7]
}
```

**状态码**:
- `200`: 更新成功
- `400`: 参数错误
- `404`: 项目不存在
- `500`: 服务器错误

---

### 3.11 更新项目文案（管理员）

**接口地址**: `PUT /api/admin/project/updateProjectCW`

**接口功能**: 全量更新项目的展示文案或学习资料

**鉴权**: 需要管理员权限

**请求参数** (Body - JSON):
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| projectId | Long | 是 | 项目ID |
| cwType | String | 是 | 文案类型 |
| cwList | List&lt;CopyWriting&gt; | 是 | 新的文案列表 |

**请求示例**:
```json
{
  "projectId": 1,
  "cwType": "LEARNING_MATERIAL",
  "cwList": [
    {
      "title": "开发文档",
      "link": "https://example.com/docs-v2.pdf",
      "note": "更新版文档"
    }
  ]
}
```

**状态码**:
- `200`: 更新成功
- `400`: 参数错误
- `404`: 项目不存在
- `500`: 服务器错误

---

## 4. 任务模块

### 4.1 获取任务分类列表

**接口地址**: `GET /api/task/fetchTaskCategories`

**接口功能**: 获取所有任务分类

**返回数据**:
```json
[
  {
    "id": 1,
    "name": "代码开发",
    "description": "编写代码相关的任务",
    "taskCount": "15"
  },
  {
    "id": 2,
    "name": "文档编写",
    "description": "编写技术文档",
    "taskCount": "8"
  }
]
```

**TaskClass字段说明**:
| 字段 | 类型 | 说明 |
|------|------|------|
| id | Long | 分类ID |
| name | String | 分类名称 |
| description | String | 分类描述 |
| taskCount | String | 该分类下的任务数量 |

**状态码**:
- `200`: 获取成功
- `204`: 无数据
- `500`: 服务器错误

---

### 4.2 按分类获取任务列表

**接口地址**: `GET /api/task/fetchTasksByCategory?categoryId={categoryId}&pageNum={pageNum}&pageSize={pageSize}&username={username}`

**接口功能**: 按分类分页获取任务列表，若提供username则标记该用户已领取的任务

**请求参数** (Query):
| 参数名 | 类型 | 必填 | 默认值 | 说明 |
|--------|------|------|--------|------|
| categoryId | Long | 是 | - | 任务分类ID |
| pageNum | Integer | 否 | 1 | 页码 |
| pageSize | Integer | 否 | 10 | 每页数量 |
| username | String | 否 | - | 用户名（用于标记已领取） |

**请求示例**:
```
GET /api/task/fetchTasksByCategory?categoryId=1&pageNum=1&pageSize=10&username=zhangsan
```

**返回数据** (`TaskListVO`):
```json
{
  "pageNum": 1,
  "pageSize": 10,
  "total": 25,
  "pages": 3,
  "taskList": [
    {
      "id": 1,
      "taskName": "开发用户登录模块",
      "taskClassName": "代码开发",
      "taskDescription": "实现用户登录功能，包括JWT认证",
      "taskStatus": 1,
      "taskProtocolTitle": "开发协议",
      "taskProtocolLink": "https://example.com/protocol.pdf",
      "collectionUser": "zhangsan",
      "collectionTime": null,
      "createTime": "2025-01-01 10:00:00",
      "updateTime": "2025-01-10 15:30:00",
      "deadlineTime": "2025-02-01 23:59:59",
      "giteeLink": "https://gitee.com/example/task1",
      "recognitionStatus": 1,
      "resultLink": null
    }
  ]
}
```

**TaskVO字段说明**:
| 字段 | 类型 | 说明 |
|------|------|------|
| id | Long | 任务ID |
| taskName | String | 任务名称 |
| taskClassName | String | 分类名称 |
| taskDescription | String | 任务描述 |
| taskStatus | Integer | 任务状态（1-待领取，2-已领取，3-已完成） |
| taskProtocolTitle | String | 任务协议标题 |
| taskProtocolLink | String | 任务协议链接 |
| collectionUser | String | 领取人用户名（已领取时显示） |
| collectionTime | String | 领取时间 |
| createTime | String | 创建时间 |
| updateTime | String | 更新时间 |
| deadlineTime | String | 截止时间 |
| giteeLink | String | Gitee项目链接 |
| recognitionStatus | Integer | 成果认定状态（1-未开始，2-进行中，3-完成） |
| resultLink | String | 提交的成果链接 |

---

### 4.3 获取我的任务列表

**接口地址**: `GET /api/task/fetchMyTasks?params={username}&pageNum={pageNum}&pageSize={pageSize}`

**接口功能**: 获取指定用户领取的任务列表

**鉴权**: 需要登录

**请求参数** (Query):
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| params | String | 是 | 用户名 |
| pageNum | String | 是 | 页码 |
| pageSize | String | 是 | 每页数量 |

**请求示例**:
```
GET /api/task/fetchMyTasks?params=zhangsan&pageNum=1&pageSize=10
```

**返回数据** (`ApiResponse<TaskListVO>`):
```json
{
  "code": 200,
  "message": "查询成功",
  "data": {
    "pageNum": 1,
    "pageSize": 10,
    "total": 5,
    "taskList": [
      {
        "id": 1,
        "taskName": "开发用户登录模块",
        "taskClassName": "代码开发",
        "taskDescription": "实现用户登录功能",
        "taskStatus": 2,
        "collectionUser": "zhangsan",
        "collectionTime": "2025-01-10 09:00:00",
        "createTime": "2025-01-01 10:00:00",
        "updateTime": "2025-01-10 15:30:00",
        "deadlineTime": "2025-02-01 23:59:59",
        "taskProtocolTitle": "开发协议",
        "taskProtocolLink": "https://example.com/protocol.pdf",
        "giteeLink": "https://gitee.com/example/task1",
        "recognitionStatus": 2,
        "resultLink": "https://example.com/result.zip"
      }
    ]
  }
}
```

---

### 4.4 获取已领取任务数量

**接口地址**: `GET /api/task/fetchReceivedTaskCount?params={username}`

**接口功能**: 获取指定用户当前已领取且未完成的任务数量

**鉴权**: 需要登录

**请求参数** (Query):
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| params | String | 是 | 用户名 |

**请求示例**:
```
GET /api/task/fetchReceivedTaskCount?params=zhangsan
```

**返回数据**:
```json
2
```
返回整数，表示已领取且���态为2（已领取）的任务数量

---

### 4.5 领取任务

**接口地址**: `PUT /api/task/claimTask`

**接口功能**: 用户领取指定任务，需满足条件：已签署实习协议、未超过最大领取数量（2个）

**鉴权**: 需要登录

**请求参数** (Body - JSON):
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| username | String | 是 | 用户名 |
| taskId | Long | 是 | 任务ID |

**请求示例**:
```json
{
  "username": "zhangsan",
  "taskId": 5
}
```

**返回数据** (`ApiResponse`):
```json
{
  "code": 200,
  "message": "任务领取成功",
  "data": null
}
```

**可能的错误**:
- `400`: 用户名为空 / 任务ID为空 / 需要先成为实习生 / 已领取该任务 / 最多只能领取2个任务
- `404`: 用户不存在 / 任务不存在
- `500`: 任务领取失败

---

### 4.6 上传任务文件

**接口地址**: `POST /api/task/uploadFile`

**接口功能**: 上传任务成果文件到MinIO存储，并记录到task_user表

**鉴权**: 需要登录（通过username参数或Header）

**请求参数** (Multipart Form):
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| taskId | Long | 是 | 任务ID |
| files | MultipartFile[] | 是 | 文件数组 |
| username | String | 否 | 用户名（也可通过Header传递） |

**Header**:
| Header名 | 说明 |
|----------|------|
| username | 可以通过Header传递用户名（支持Bearer token格式） |

**请求示例**:
```
POST /api/task/uploadFile
Content-Type: multipart/form-data

taskId: 1
files: [file1.zip, file2.pdf]
username: zhangsan
```

**返回数据**:
```json
{
  "success": true,
  "message": "文件上传成功",
  "bucket": "opensource-bucket",
  "prefix": "task-uploads/zhangsan-1/",
  "items": [
    {
      "filename": "result.zip",
      "objectName": "task-uploads/zhangsan-1/1704096000000_result.zip",
      "url": "https://minio.example.com/opensource-bucket/task-uploads/zhangsan-1/1704096000000_result.zip",
      "size": 1024000
    }
  ],
  "storedLink": "https://minio.example.com/opensource-bucket/task-uploads/zhangsan-1/1704096000000_result.zip"
}
```

**返回字段说明**:
| 字段 | 类型 | 说明 |
|------|------|------|
| success | Boolean | 上传是否成功 |
| message | String | 提示信息 |
| bucket | String | MinIO桶名称 |
| prefix | String | 文件存储路径前缀 |
| items | Array | 上传文件列表 |
| storedLink | String | 存储在数据库的链接（第一个文件的URL） |

**状态码**:
- `200`: 上传成功
- `400`: 参数错误
- `500`: 上传失败

---

### 4.7 获取成果发布模板地址

**接口地址**: `GET /api/task/getPublishTemplateUrl`

**接口功能**: 获取任务成果发布模板的下载地址

**返回数据**:
```json
{
  "url": "https://example.com/templates/result-template.docx"
}
```

**状态码**:
- `200`: 获取成功
- `500`: 获取失败

---

## 5. 活动模块

### 5.1 获取活动列表

**接口地址**: `GET /api/events?q={keyword}&type={type}&tag={tag}&page={page}&pageSize={pageSize}`

**接口功能**: 分页查询活动列表，支持关键词、类型、标签筛选

**请求参数** (Query):
| 参数名 | 类型 | 必填 | 默认值 | 说明 |
|--------|------|------|--------|------|
| q | String | 否 | - | 搜索关键词（搜索标题和摘要） |
| type | String | 否 | - | 活动类型 |
| tag | String | 否 | - | 活动标签 |
| page | Integer | 否 | 1 | 页码 |
| pageSize | Integer | 否 | 10 | 每页数量 |

**请求示例**:
```
GET /api/events?q=开源&type=conference&page=1&pageSize=10
```

**返回数据** (`ApiResponse<Map>`):
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "list": [
      {
        "id": 1,
        "slug": "open-source-summit-2025",
        "title": "2025开源峰会",
        "summary": "年度最大规模的开源技术峰会",
        "coverUrl": "https://example.com/cover.jpg",
        "type": "conference",
        "status": 1,
        "startTime": "2025-06-01 09:00:00",
        "endTime": "2025-06-03 18:00:00",
        "city": "北京",
        "location": "国家会议中心",
        "online": false,
        "tags": "开源,技术,峰会",
        "viewCount": 1250,
        "ctaText": "立即报名",
        "ctaUrl": "https://example.com/register",
        "detailUrl": null,
        "detailIsExternal": false
      }
    ],
    "total": 50,
    "page": 1,
    "pageSize": 10
  }
}
```

**Event字段说明**:
| 字段 | 类型 | 说明 |
|------|------|------|
| id | Long | 活动ID |
| slug | String | 活动唯一标识（URL友好） |
| title | String | 活动标题 |
| summary | String | 活动摘要 |
| coverUrl | String | 封面图URL |
| type | String | 活动类型 |
| status | Integer | 活动状态 |
| startTime | String | 开始时间 |
| endTime | String | 结束时间 |
| city | String | 城市 |
| location | String | 具体地点 |
| online | Boolean | 是否线上活动 |
| tags | String | 标签（逗号分隔） |
| viewCount | Integer | 浏览次数 |
| ctaText | String | 行动按钮文本 |
| ctaUrl | String | 行动按钮链接 |
| detailUrl | String | 详情页链接（为空则使用/events/:slug） |
| detailIsExternal | Boolean | 详情链接是否为外链 |

---

### 5.2 获取活动详情

**接口地址**: `GET /api/events/{slug}`

**接口功能**: 根据slug获取活动详细信息

**请求参数** (Path):
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| slug | String | 是 | 活动唯一标识 |

**请求示例**:
```
GET /api/events/open-source-summit-2025
```

**返回数据** (`ApiResponse<Map>`):
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "id": 1,
    "slug": "open-source-summit-2025",
    "title": "2025开源峰会",
    "summary": "年度最大规模的开源技术峰会",
    "coverUrl": "https://example.com/cover.jpg",
    "type": "conference",
    "status": 1,
    "startTime": "2025-06-01 09:00:00",
    "endTime": "2025-06-03 18:00:00",
    "city": "北京",
    "location": "国家会议中心",
    "online": false,
    "tags": "开源,技术,峰会",
    "templateId": "template-001",
    "blocks": "[{\"type\":\"text\",\"content\":\"...\"}]",
    "contentMd": "# 活动介绍\n\n这是一场...",
    "speakers": "[{\"name\":\"张三\",\"title\":\"技术专家\"}]",
    "agenda": "[{\"time\":\"09:00\",\"topic\":\"开幕致辞\"}]",
    "gallery": "[\"https://example.com/photo1.jpg\"]",
    "seo": "{\"keywords\":\"开源,峰会\",\"description\":\"...\"}",
    "viewCount": 1251,
    "ctaText": "立即报名",
    "ctaUrl": "https://example.com/register"
  }
}
```

**详情中的JSON字段**:
| 字段 | 类型 | 说明 |
|------|------|------|
| blocks | String(JSON) | 页面内容块配置 |
| contentMd | String | Markdown格式的内容 |
| speakers | String(JSON) | 演讲嘉宾列表 |
| agenda | String(JSON) | 活动议程 |
| gallery | String(JSON) | 图片画廊 |
| seo | String(JSON) | SEO配置 |

---

### 5.3 获取活动元数据

**接口地址**: `GET /api/events/meta`

**接口功能**: 获取活动的元数据信息，包括活动类型列表和热门标签

**返回数据** (`ApiResponse<EventMetaDTO>`):
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "types": [
      {
        "value": "conference",
        "label": "会议"
      },
      {
        "value": "workshop",
        "label": "工作坊"
      },
      {
        "value": "meetup",
        "label": "聚会"
      }
    ],
    "hotTags": [
      "开源",
      "技术",
      "峰会",
      "云原生",
      "AI"
    ]
  }
}
```

**EventMetaDTO字段说明**:
| 字段 | 类型 | 说明 |
|------|------|------|
| types | List&lt;TypeVO&gt; | 活动类型列表 |
| hotTags | List&lt;String&gt; | 热门标签列表 |

**TypeVO字段说明**:
| 字段 | 类型 | 说明 |
|------|------|------|
| value | String | 类型值 |
| label | String | 类型显示名称 |

---

### 5.4 创建活动（管理员）

**接口地址**: `POST /api/admin/events`

**接口功能**: 创建新的活动

**鉴权**: 需要管理员权限

**请求参数** (Body - JSON): Event对象的所有字段

**请求示例**:
```json
{
  "slug": "new-event-2025",
  "title": "新活动",
  "summary": "活动简介",
  "coverUrl": "https://example.com/cover.jpg",
  "type": "meetup",
  "status": 1,
  "startTime": "2025-03-01 14:00:00",
  "endTime": "2025-03-01 18:00:00",
  "city": "上海",
  "location": "某某会议室",
  "online": true,
  "tags": "技术,分享"
}
```

**返回数据** (`ApiResponse<Long>`):
```json
{
  "code": 200,
  "message": "操作成功",
  "data": 10
}
```
返回新创建活动的ID

---

### 5.5 更新活动（管理员）

**接口地址**: `PUT /api/admin/events/{id}`

**接口功能**: 更新指定活动的信息

**鉴权**: 需要管理员权限

**请求参数**:
- **Path参数**:
  | 参数名 | 类型 | 必填 | 说明 |
  |--------|------|------|------|
  | id | Long | 是 | 活动ID |

- **Body参数**: Event对象的字段

**返回数据** (`ApiResponse<Integer>`):
```json
{
  "code": 200,
  "message": "操作成功",
  "data": 1
}
```
返回受影响的行数

---

## 6. 文档模块

### 6.1 查询文档列表

**接口地址**: `POST /api/docs`

**接口功能**: 根据条件查询文档列表

**请求参数** (Body - JSON):
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| arch | String | 否 | 架构类型 |
| manufacturer | String | 否 | 厂商 |
| series | String | 否 | 系列 |

**请求示例**:
```json
{
  "arch": "x86",
  "manufacturer": "Intel"
}
```

**返回数据**:
```json
[
  {
    "id": 1,
    "title": "Intel x86 开发手册",
    "url": "https://example.com/intel-x86-manual.pdf",
    "arch": "x86",
    "manufacturer": "Intel",
    "series": "Core"
  }
]
```

**Document字段说明**:
| 字段 | 类型 | 说明 |
|------|------|------|
| id | Long | 文档ID |
| title | String | 文档标题 |
| url | String | 文档链接 |
| arch | String | 架构类型 |
| manufacturer | String | 厂商 |
| series | String | 系列 |

**状态码**:
- `200`: 查询成功
- `204`: 无数据
- `400`: 参数错误
- `500`: 服务器错误

---

### 6.2 获取文档菜单

**接口地址**: `GET /api/docs_menu`

**接口功能**: 获取文档导航菜单结构

**返回数据**:
```json
[
  {
    "id": 1,
    "level": "1",
    "title": "开发文档",
    "description": "各类开发文档汇总",
    "url": null,
    "icon": "document",
    "order": 1
  },
  {
    "id": 2,
    "level": "2",
    "title": "架构指南",
    "description": "系统架构设计文档",
    "url": "/docs/architecture",
    "icon": "architecture",
    "order": 2
  }
]
```

**Menu字段说明**:
| 字段 | 类型 | 说明 |
|------|------|------|
| id | Long | 菜单ID |
| level | String | 菜单层级 |
| title | String | 菜单标题 |
| description | String | 菜单描述 |
| url | String | 菜单链接 |
| icon | String | 图标标识 |
| order | Integer | 排序序号 |

**状态码**:
- `200`: 获取成功
- `204`: 无数据
- `500`: 服务器错误

---

## 7. 镜像模块

### 7.1 查询镜像列表

**接口地址**: `POST /api/mirrors`

**接口功能**: 根据条件查询镜像列表

**请求参数** (Body - JSON):
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| arch | String | 否 | 架构类型 |
| manufacturer | String | 否 | 厂商 |
| series | String | 否 | 系列 |

**请求示例**:
```json
{
  "arch": "arm64",
  "manufacturer": "Ubuntu"
}
```

**返回数据**:
```json
[
  {
    "id": 1,
    "name": "ubuntu-22.04-arm64",
    "urlOut": "https://mirrors.example.com/ubuntu/22.04/arm64",
    "urlInter": "https://internal.example.com/ubuntu/22.04/arm64",
    "size": "2.5GB",
    "time": "2025-01-01",
    "arch": "arm64",
    "manufacturer": "Ubuntu",
    "series": "22.04"
  }
]
```

**Mirror字段说明**:
| 字段 | 类型 | 说明 |
|------|------|------|
| id | Long | 镜像ID |
| name | String | 镜像名称 |
| urlOut | String | 外网下载地址 |
| urlInter | String | 内网下载地址 |
| size | String | 镜像大小 |
| time | String | 更新时间 |
| arch | String | 架构类型 |
| manufacturer | String | 厂商 |
| series | String | 系列 |

**状态码**:
- `200`: 查询成功
- `204`: 无数据
- `400`: 参数错误
- `500`: 服务器错误

---

### 7.2 获取镜像菜单

**接口地址**: `GET /api/mirrors_menu`

**接口功能**: 获取镜像分类菜单

**返回数据**: 返回Menu对象列表（结构同文档菜单）

---

## 8. 首页模块

### 8.1 获取首页轮播图

**接口地址**: `GET /api/public/home_carousel`

**接口功能**: 获取首页轮播图列表

**返回数据**:
```json
[
  {
    "id": 1,
    "title": "欢迎来到开源社区",
    "context": "这是一个开源爱好者的聚集地",
    "image": "https://example.com/carousel1.jpg"
  },
  {
    "id": 2,
    "title": "最新开源项目",
    "context": "查看我们最新的开源项目",
    "image": "https://example.com/carousel2.jpg"
  }
]
```

**HomeCarousel字段说明**:
| 字段 | 类型 | 说明 |
|------|------|------|
| id | Long | 轮播图ID |
| title | String | 标题 |
| context | String | 描述内容 |
| image | String | 图片URL |

**状态码**:
- `200`: 获取成功
- `204`: 无数据
- `500`: 服务器错误

---

## 9. 关于模块

### 9.1 获取关于页面文本

**接口地址**: `GET /api/public/about_text?area={area}`

**接口功能**: 获取指定区域的关于页面文本内容

**请求参数** (Query):
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| area | String | 是 | 区域标识 |

**请求示例**:
```
GET /api/public/about_text?area=introduction
```

**返回数据**: 直接返回文本内容（String）
```
这是关于我们的介绍文本内容...
```

**状态码**:
- `200`: 获取成功
- `400`: 区域参数不能为空
- `404`: 未找到对应区域的文本内容
- `500`: 服务器错误

---

## 10. 验证码模块

### 10.1 生成图形验证码

**接口地址**: `GET /api/public/captcha/{email}`

**接口功能**: 生成图形验证码并存储到Redis，有效期5分钟

**请求参数** (Path):
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| email | String | 是 | 邮箱地址（作为Redis key） |

**请求示例**:
```
GET /api/public/captcha/zhangsan@example.com
```

**返回数据**: 直接返回验证码图片（image/jpeg格式）

**说明**:
- 验证码存储在Redis中，key为邮箱地址，value为验证码文本
- 有效期为5分钟
- 用于忘记密码流程的验证

---

## 11. 管理员模块

### 11.1 查询用户列表

**接口地址**: `POST /api/admin/select_user`

**接口功能**: 根据用户名或真实姓名分页查询用户

**鉴权**: 需要管理员权限

**请求参数** (Body - JSON):
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| username | String | 否 | 用户名（模糊查询） |
| fullname | String | 否 | 真实姓名（模糊查询） |
| pageNum | Integer | 否 | 页码 |
| pageSize | Integer | 否 | 每页数量 |

**请求示例**:
```json
{
  "username": "zhang",
  "pageNum": 1,
  "pageSize": 10
}
```

**返回数据** (`PageResultVO<User>`):
```json
{
  "list": [
    {
      "id": 1,
      "username": "zhangsan",
      "email": "zhangsan@example.com",
      "fullname": "张三",
      "role": "USER",
      "enabled": true,
      "hasSignedPdf": true,
      "phone": "13800138000"
    }
  ],
  "total": 15
}
```

**PageResultVO字段说明**:
| 字段 | 类型 | 说明 |
|------|------|------|
| list | List&lt;T&gt; | 数据列表 |
| total | Long | 总记录数 |

**状态码**:
- `200`: 查询成功
- `500`: 服务器错误

---

### 11.2 更新用户信息

**接口地址**: `POST /api/admin/update_user`

**接口功能**: 管理员更新用户信息

**鉴权**: 需要管理员权限

**请求参数** (Body - JSON): RequestParamDTO对象的用户相关字段

**请求示例**:
```json
{
  "username": "zhangsan",
  "fullname": "张三更新",
  "role": "USER",
  "hasSignedPdf": true,
  "phone": "13900139000"
}
```

**返回数据**:
```
更新用户成功
```

**状态码**:
- `200`: 更新成功
- `500`: 更新失败

---

### 11.3 删除用户

**接口地址**: `DELETE /api/admin/delete_user?id={id}`

**接口功能**: 删除指定用户

**鉴权**: 需要管理员权限

**请求参数** (Query):
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | Long | 是 | 用户ID |

**请求示例**:
```
DELETE /api/admin/delete_user?id=10
```

**返回数据**:
```
用户删除成功
```

**状态码**:
- `200`: 删除成功
- `400`: 用户ID不能为空
- `404`: 用户不存在
- `500`: 服务器错误

---

### 11.4 查询任务列表（管理员）

**接口地址**: `POST /api/admin/select_task`

**接口功能**: 管理员分页查询任务及其领取情况

**鉴权**: 需要管理员权限

**请求参数** (Body - JSON):
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| taskName | String | 否 | 任务名称（模糊查询） |
| collectionUser | String | 否 | 领取人用户名（模糊查询） |
| taskStatus | Integer | 否 | 任务状态 |
| pageNum | Integer | 否 | 页码 |
| pageSize | Integer | 否 | 每页数量 |

**请求示例**:
```json
{
  "taskName": "登录",
  "taskStatus": 2,
  "pageNum": 1,
  "pageSize": 10
}
```

**返回数据** (`PageResultVO<AdminTaskVO>`):
```json
{
  "list": [
    {
      "taskUserId": 5,
      "taskName": "开发用户登录模块",
      "taskClassName": "代码开发",
      "collectionUser": "zhangsan",
      "phone": "13800138000",
      "email": "zhangsan@example.com",
      "email2": "backup@example.com",
      "collectionTime": "2025-01-10 09:00:00",
      "createTime": "2025-01-01 10:00:00",
      "deadlineTime": "2025-02-01 23:59:59",
      "taskStatus": 2,
      "recogStatus": 2,
      "resultLink": "https://example.com/result.zip"
    }
  ],
  "total": 50
}
```

**AdminTaskVO字段说明**:
| 字段 | 类型 | 说明 |
|------|------|------|
| taskUserId | Long | 任务用户关系ID |
| taskName | String | 任务名称 |
| taskClassName | String | 任务分类 |
| collectionUser | String | 领取人用户名 |
| phone | String | 领取人手机 |
| email | String | 领取人邮箱 |
| email2 | String | 领取人备用邮箱 |
| collectionTime | String | 领取时间 |
| createTime | String | 任务创建时间 |
| deadlineTime | String | 截止时间 |
| taskStatus | Integer | 任务状态 |
| recogStatus | Integer | 成果认定状态 |
| resultLink | String | 成果链接 |

**状态码**:
- `200`: 查询成功
- `500`: 服务器错误

---

### 11.5 更新任务状态（管理员）

**接口地址**: `PUT /api/admin/update_task_status`

**接口功能**: 管理员更新任务的状态或成果认定状态

**鉴权**: 需要管理员权限

**请求参数** (Body - JSON):
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| taskUserId | Long | 是 | 任务用户关系ID |
| taskStatus | Integer | 否 | 任务状态（1-审核中，2-进行中，3-结束，4-关闭） |
| recogStatus | Integer | 否 | 成果认定状态（1-未开始，2-进行中，3-完成） |

**请求示例**:
```json
{
  "taskUserId": 5,
  "taskStatus": 3,
  "recogStatus": 3
}
```

**返回数据**:
```
任务状态更新成功
```

**状态码**:
- `200`: 更新成功
- `400`: 参数不能为空或更新失败
- `500`: 服务器错误

---

## 附录

### A. 任务状态枚举

| 状态码 | 说明 |
|--------|------|
| 1 | 待领取/审核中 |
| 2 | 已领取/进行中 |
| 3 | 已完成/结束 |
| 4 | 关闭 |

### B. 成果认定状态枚举

| 状态码 | 说明 |
|--------|------|
| 1 | 未开始 |
| 2 | 进行中 |
| 3 | 完成 |

### C. 用户角色枚举

| 角色 | 说明 |
|------|------|
| USER | 普通用户 |
| ADMIN | 管理员 |

### D. 文案区域标识示例

| 区域标识 | 说明 |
|----------|------|
| internship-agreement | 实习协议 |
| word-template | 成果发布模板 |
| PROJECT_DISPLAY | 项目展示 |
| LEARNING_MATERIAL | 学习资料 |

---

## 版本记录

| 版本 | 日期 | 说明 |
|------|------|------|
| v1.0 | 2025-01-20 | 初始版本 |

---

**文档生成时间**: 2025-10-29
**后端框架**: Spring Boot
**数据库**: MySQL
**缓存**: Redis
