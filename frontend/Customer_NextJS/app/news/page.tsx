'use client'
import React, { useState } from 'react'

const newsData = [
    { date: "29.03.2024", title: "SavorGO KHUẤY ĐẢO SAIGON TATTOO EXPO 4", description: "Mãn nhãn – Sôi động – Bùng nổ là những từ miêu tả…", image: "/images/hcm1.jpg" },
    { date: "28.02.2024", title: "TIỆM NƯỚNG CUỒNG NỘ BY SavorGO – BUFFET CHỈ TỪ 199K", description: "Không ngừng phát triển và hoàn thiện phong cách, SavorGO chính thức…", image: "/images/hcm2.jpg" },
    { date: "26.02.2024", title: "SavorGO – NHÀ TÀI TRỢ ĐẶC BIỆT CỦA YÊU HÒA BÌNH 2023", description: "Xuất phát điểm từ những người yêu âm nhạc, SavorGO được thành…", image: "/images/hcm3.png" },
    { date: "20.02.2024", title: "SỰ KIỆN MỚI TẠI SavorGO", description: "Hãy cùng chờ đón những sự kiện hấp dẫn sắp tới…", image: "/images/hcm4.jpg" },
    { date: "15.02.2024", title: "ĐÊM NHẠC CỰC CHÁY TẠI SavorGO", description: "Bùng cháy cùng những nghệ sĩ hàng đầu…", image: "/images/phanthiet1.png" },
    { date: "10.02.2024", title: "CHƯƠNG TRÌNH KHUYẾN MÃI ĐẶC BIỆT", description: "Những ưu đãi không thể bỏ lỡ đang chờ bạn…", image: "/images/nhatrang.jpg" },
    { date: "05.02.2024", title: "SỰ KIỆN ĐẶC BIỆT TẠI SavorGO", description: "Những khoảnh khắc khó quên đang đến gần…", image: "/images/nhatrang1.jpg" },
    { date: "01.02.2024", title: "ƯU ĐÃI LỚN DÀNH CHO THÀNH VIÊN", description: "Những đặc quyền hấp dẫn cho thành viên VIP…", image: "/images/nhatrang2.jpg" },
    { date: "28.01.2024", title: "CHƯƠNG TRÌNH MỚI TẠI SavorGO", description: "Sự kiện giải trí đẳng cấp đang chờ đón bạn…", image: "/images/nhatrang.jpg" }
];

const News = () => {
    const [currentPage, setCurrentPage] = useState(1);

    const getItemsPerPage = (page: number): number => (page === 1 ? 6 : 3);

    const itemsPerPage = getItemsPerPage(currentPage);

    const totalPages = Math.ceil((newsData.length - 6) / 3) + 1;

    const startIndex = currentPage === 1 ? 0 : 6 + (currentPage - 2) * 3;
    const endIndex = startIndex + itemsPerPage;
    const currentNews = newsData.slice(startIndex, endIndex);

    return (
        <div className="bg-white text-white py-10 px-6 " style={{ paddingRight: '5px' }}>
            <div className="flex justify-center mb-10">
                <div className="bg-yellow-500 text-black font-bold px-6 py-2 rounded-lg text-lg border-4 border-black">
                    CHUYỆN GÌ ĐÂY?
                </div>
            </div>
            <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
                {currentNews.map((news, index) => (
                    <div key={index} className="bg-gray-900 p-4 rounded-lg">
                        <img src={news.image} alt={news.title} className="w-full h-48 object-cover rounded-md" />
                        <p className="text-yellow-400 mt-3">{news.date}</p>
                        <h3 className="text-lg font-bold mt-2">{news.title}</h3>
                        <p className="text-gray-400 mt-2">{news.description}</p>
                    </div>
                ))}
            </div>
            <div className="flex justify-center mt-6">
                {[...Array(totalPages)].map((_, index) => (
                    <button
                        key={index}
                        className={`mx-1 px-3 py-2 border rounded-lg ${currentPage === index + 1 ? "bg-yellow-500 text-black" : "border-yellow-500 text-yellow-500"}`}
                        onClick={() => setCurrentPage(index + 1)}
                    >
                        {index + 1}
                    </button>
                ))}
            </div>
        </div>
    );
};

export default News;
