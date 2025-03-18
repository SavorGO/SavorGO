"use client";

import { useEffect, useState } from "react";
import { useParams } from "next/navigation";

interface MenuItem {
  id: string;
  image: string;
  name: string;
  price: number;
  description: string;
}

export default function MenuDetail() {
  const { id } = useParams(); // Lấy id từ URL
  const [menuItem, setMenuItem] = useState<MenuItem | null>(null);

  useEffect(() => {
    const fetchMenuItem = async () => {
      try {
        const res = await fetch(`/api/menu/${id}`); // Gọi API lấy món ăn theo id
        if (!res.ok) throw new Error("Không tìm thấy món ăn");
        const data = await res.json();
        setMenuItem(data);
      } catch (error) {
        console.error("Lỗi:", error);
      }
    };
    if (id) fetchMenuItem();
  }, [id]);

  if (!menuItem) {
    return <p className="text-center text-gray-500">Đang tải...</p>;
  }

  return (
    <div className="p-6">
      <h1 className="text-2xl font-bold">{menuItem.name}</h1>
      <img src={menuItem.image} alt={menuItem.name} className="w-full h-80 object-cover mt-4" />
      <p className="text-gray-600 mt-4">{menuItem.description}</p>
      <p className="text-xl font-semibold mt-2">{menuItem.price.toLocaleString()} VND</p>
    </div>
  );
}
