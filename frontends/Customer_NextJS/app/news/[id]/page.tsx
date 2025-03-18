"use client";
import { useRouter } from "next/navigation";
import { useEffect, useState } from "react";

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

const NewsDetail = ({ params }: { params: { id: string } }) => {
    const router = useRouter();
    const [newsItem, setNewsItem] = useState<any>(null);

    useEffect(() => {
        const newsId = parseInt(params.id, 10);
        if (!isNaN(newsId) && newsId >= 0 && newsId < newsData.length) {
            setNewsItem(newsData[newsId]);
        } else {
            setNewsItem(null);
        }
    }, [params.id]);

    if (!newsItem) {
        return (
            <div className="flex flex-col items-center justify-center h-screen">
                <h1 className="text-2xl font-bold text-red-500">Bài viết không tồn tại!</h1>
                <button 
                    className="mt-4 px-4 py-2 bg-yellow-500 text-black rounded-lg font-bold"
                    onClick={() => router.push("/news")}
                >
                    Quay lại danh sách
                </button>
            </div>
        );
    }

    return (
        <div className="max-w-3xl mx-auto py-10 px-6">
            <button 
                className="mb-4 px-4 py-2 bg-yellow-500 text-black rounded-lg font-bold"
                onClick={() => router.push("/news")}
            >
                Quay lại
            </button>
            <div className="bg-gray-900 p-6 rounded-lg">
                <img src={newsItem.image} alt={newsItem.title} className="w-full h-64 object-cover rounded-md" />
                <p className="text-yellow-400 mt-3">{newsItem.date}</p>
                <h1 className="text-2xl font-bold mt-2">{newsItem.title}</h1>
                <p className="text-gray-400 mt-4">{newsItem.description}</p>
            </div>
        </div>
    );
};

export default NewsDetail;
