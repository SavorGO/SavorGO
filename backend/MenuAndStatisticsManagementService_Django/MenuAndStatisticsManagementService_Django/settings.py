"""
Django settings for MenuAndStatisticsManagementService_Django project.

Generated by 'django-admin startproject' using Django 5.1.4.

For more information on this file, see
https://docs.djangoproject.com/en/5.1/topics/settings/

For the full list of settings and their values, see
https://docs.djangoproject.com/en/5.1/ref/settings/
"""

from pathlib import Path
import os

# Build paths inside the project like this: BASE_DIR / 'subdir'.
BASE_DIR = Path(__file__).resolve().parent.parent


# Quick-start development settings - unsuitable for production
# See https://docs.djangoproject.com/en/5.1/howto/deployment/checklist/

# SECURITY WARNING: keep the secret key used in production secret!
SECRET_KEY = 'django-insecure-0r@$r6v8c9ob76d@ck@@z6_#ck-klqn!h+t1ou8oy8lf-@v=2j'

# SECURITY WARNING: don't run with debug turned on in production!
DEBUG = True

ALLOWED_HOSTS = []


# Application definition

INSTALLED_APPS = [
    # Các app mặc định của Django
    'django.contrib.admin',
    'django.contrib.auth',
    'django.contrib.contenttypes',
    'django.contrib.sessions',
    'django.contrib.messages',
    'django.contrib.staticfiles',
    'django_mongoengine',  # Đảm bảo đã thêm django_mongoengine vào INSTALLED_APPS
    # Thêm app rest_framework
    'rest_framework',
    'drf_spectacular',
    'drf_spectacular_sidecar',  # Để có Swagger UI
    # Các app khác của bạn
    'table_management',
    'menu_management',
    'promotion_management',
]

REST_FRAMEWORK = {
    'DEFAULT_SCHEMA_CLASS': 'drf_spectacular.openapi.AutoSchema',
    'EXCEPTION_HANDLER': 'MenuAndStatisticsManagementService_Django.exceptions.custom_exception_handler'
}

import os

LOGGING = {
    "version": 1,
    "disable_existing_loggers": False,
    "formatters": {
        "verbose": {
            "format": "{levelname} {asctime} {module} {message}",
            "style": "{",
        },
        "simple": {
            "format": "{levelname} {message}",
            "style": "{",
        },
    },
    "handlers": {
        "file": {
            "level": "DEBUG",
            "class": "logging.FileHandler",
            "filename": os.path.join(BASE_DIR, "logs", "api.log"),  # Log API riêng
            "formatter": "verbose",
        },
        "console": {
            "level": "INFO",
            "class": "logging.StreamHandler",
            "formatter": "simple",
        },
    },
    "loggers": {
        "django": {  # Giảm mức log của Django core
            "handlers": ["file"],
            "level": "WARNING",  # Chỉ log WARNING trở lên
            "propagate": False,
        },
        "myapp.api": {  # Logger riêng cho API
            "handlers": ["file", "console"],
            "level": "DEBUG",
            "propagate": False,
        },
    },
}

APPEND_SLASH = False

DEFAULT_INDEX_TABLESPACE = None  # Ngăn Django ORM xử lý MongoEngine model
MIDDLEWARE = [
    'django.middleware.security.SecurityMiddleware',
    'django.contrib.sessions.middleware.SessionMiddleware',
    'django.middleware.common.CommonMiddleware',
    'django.middleware.csrf.CsrfViewMiddleware',
    'django.contrib.auth.middleware.AuthenticationMiddleware',
    'django.contrib.messages.middleware.MessageMiddleware',
    'django.middleware.clickjacking.XFrameOptionsMiddleware',
    
    # Thêm middleware bắt lỗi toàn cục
    'MenuAndStatisticsManagementService_Django.middlewares.GlobalExceptionMiddleware',
    'MenuAndStatisticsManagementService_Django.middlewares.AppendTimestampMiddleware',
]


ROOT_URLCONF = 'MenuAndStatisticsManagementService_Django.urls'

TEMPLATES = [
    {
        'BACKEND': 'django.template.backends.django.DjangoTemplates',
        'DIRS': [],
        'APP_DIRS': True,
        'OPTIONS': {
            'context_processors': [
                'django.template.context_processors.debug',
                'django.template.context_processors.request',
                'django.contrib.auth.context_processors.auth',
                'django.contrib.messages.context_processors.messages',
            ],
        },
    },
]

WSGI_APPLICATION = 'MenuAndStatisticsManagementService_Django.wsgi.application'


# Database
# https://docs.djangoproject.com/en/5.1/ref/settings/#databases

DATABASES = {
    'default': {  # MariaDB
        'ENGINE': 'django.db.backends.mysql',
        'NAME': 'SavorGO',  # Tên cơ sở dữ liệu MariaDB
        'USER': 'root',  # Tên người dùng MariaDB
        'PASSWORD': 'sapassword',  # Mật khẩu
        'HOST': 'localhost',  # Máy chủ
        'PORT': '3306',  # Cổng MariaDB
    }
}
MONGODB_DATABASES = {
    'default': {
        'name': 'SavorGO',  # Tên database MongoDB
        'host': 'localhost',  # Địa chỉ host MongoDB
        'port': 27017,  # Cổng kết nối MongoDB (mặc định là 27017)
        'username': '',  # Tên người dùng MongoDB nếu cần
        'password': '',  # Mật khẩu MongoDB nếu cần
    }
}

from MenuAndStatisticsManagementService_Django.database_routers import MariaDBRouter, MongoDBRouter

DATABASE_ROUTERS = [MariaDBRouter(), MongoDBRouter()]
import mongoengine


# Cấu hình kết nối MongoDB
mongoengine.connect(
    db='SavorGO',  # Tên database MongoDB
    host='localhost',         # Địa chỉ host MongoDB
    port=27017,               # Cổng kết nối MongoDB (mặc định là 27017)
)
# Password validation
# https://docs.djangoproject.com/en/5.1/ref/settings/#auth-password-validators

AUTH_PASSWORD_VALIDATORS = [
    {
        'NAME': 'django.contrib.auth.password_validation.UserAttributeSimilarityValidator',
    },
    {
        'NAME': 'django.contrib.auth.password_validation.MinimumLengthValidator',
    },
    {
        'NAME': 'django.contrib.auth.password_validation.CommonPasswordValidator',
    },
    {
        'NAME': 'django.contrib.auth.password_validation.NumericPasswordValidator',
    },
]


# Internationalization
# https://docs.djangoproject.com/en/5.1/topics/i18n/

LANGUAGE_CODE = 'en-us'

TIME_ZONE = 'UTC'

USE_I18N = True

USE_TZ = True


# Static files (CSS, JavaScript, Images)
# https://docs.djangoproject.com/en/5.1/howto/static-files/

STATIC_URL = 'static/'

# Default primary key field type
# https://docs.djangoproject.com/en/5.1/ref/settings/#default-auto-field

DEFAULT_AUTO_FIELD = 'django.db.models.BigAutoField'
