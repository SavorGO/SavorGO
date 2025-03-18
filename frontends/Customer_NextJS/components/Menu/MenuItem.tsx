"use client"; // Nếu dùng Next.js App Router (app directory)

import React from "react";
import { useRouter } from "next/navigation"; // Dùng useRouter của Next.js

interface MenuItemProps {
  image: string;
  name: string;
  price: number;
  id: string;
}

const MenuItem: React.FC<MenuItemProps> = ({ image, name, price, id }) => {
  const router = useRouter();

  return (
    <div
      className="w-[300px] rounded-lg overflow-hidden shadow-md bg-white cursor-pointer hover:shadow-lg transition"
      onClick={() => router.push(`/menu/${id}`)}
    >
      <img src={image} alt={name} className="w-full h-40 object-cover" />
      <div className="p-3 text-center">
        <h3 className="text-lg font-semibold">{name}</h3>
        <p className="text-gray-600">{price.toLocaleString()} VND</p>
      </div>
    </div>
  );
};

export default MenuItem;
