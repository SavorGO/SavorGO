# ai/ai_query.py

from langchain_community.llms import Ollama
from langchain_community.utilities import SQLDatabase
from langchain_experimental.sql import SQLDatabaseChain
from dotenv import load_dotenv
import os

# Load biến môi trường từ .env
load_dotenv()

# Kết nối DB MariaDB từ .env
db = SQLDatabase.from_uri(
    "mysql+pymysql://root:sapassword@localhost:3306/SavorGO"
)


# Khởi tạo mô hình Llama3 đang chạy qua Ollama
llm = Ollama(model="llama3")

# Tạo chuỗi LangChain truy vấn DB qua AI
db_chain = SQLDatabaseChain.from_llm(llm, db, verbose=True)

# Câu hỏi tự nhiên
question = "Danh sách khuyến mãi đang hoạt động hôm nay?"

# Gửi tới AI + DB
result = db_chain.run(question)
print("🧠 Kết quả từ AI:")
print(result)
