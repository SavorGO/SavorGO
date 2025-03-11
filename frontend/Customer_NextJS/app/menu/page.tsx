"use client";

import React, { useEffect, useState } from "react";
import MenuItem from "@/components/Menu/MenuItem";

const MenuPage = () => {
  const [menu, setMenu] = useState([]);
  const [searchTerm, setSearchTerm] = useState("");

  useEffect(() => {
    const fetchMenu = async () => {
      try {
        const res = await fetch("http://127.0.0.1:8000/api/menus?page=1&size=20&sortBy=id&sortDirection=asc&statusFilter=without_deleted");
        if (!res.ok) throw new Error("Lỗi khi lấy thực đơn");
        const result = await res.json();
        
        // Kiểm tra nếu API trả về dữ liệu hợp lệ
        if (result.status === 200 && result.data && Array.isArray(result.data.data)) {
          setMenu(result.data.data);
        } else {
          throw new Error("Dữ liệu API không hợp lệ");
        }
        console.log("API Result:", result); // In kết quả API để kiểm tra

      } catch (error) {
        console.error("Lỗi:", error);
      }
    };
    
    fetchMenu();
  }, []);

  const filteredMenu = menu.filter((item) =>
    item.name.toLowerCase().includes(searchTerm.toLowerCase())
  );

  return (
    <div className="p-6">
      <div className="flex justify-between items-center mb-10">
        <h1 className="text-2xl font-bold">Thực Đơn</h1>
        <div className="flex items-center border border-gray-300 rounded-lg bg-white w-[800px]">
          <input
            type="text"
            placeholder="Tìm kiếm món ăn..."
            className="w-full h-10 px-3 rounded-l-lg outline-none"
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
          />
          <button className="h-10 w-12 bg-white text-white rounded-r-lg cursor-pointer hover:bg-black flex items-center justify-center">
          </button>
        </div>
      </div>

      <div className="grid grid-cols-3 gap-6 w-[1190px]">
        {filteredMenu.length > 0 ? (
          filteredMenu.map((item) => (
            <MenuItem
              key={item.id}
              id={item.id}
              image={'https://res.cloudinary.com/dtgzcw63e/image/upload/c_scale,h_190,w_340/r_0/v1740734001/SavorGO/Menus/' + item.public_id}// Nếu ảnh lưu trong public_id, cần kiểm tra lại
              name={item.name}
              price={item.discounted_price || item.sale_price || item.original_price} // Hiển thị giá phù hợp
            />
          ))
        ) : (
          <p className="col-span-3 text-center text-gray-500">
            Không tìm thấy món ăn nào
          </p>
        )}
      </div>
    </div>
  );
};

export default MenuPage;
