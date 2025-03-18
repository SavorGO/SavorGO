"use client";
import React from "react";
import { useRouter } from "next/navigation";

const Login = () => {
  const router = useRouter();

  return (
    <div className="flex justify-center items-center h-500px w-screen bg-white pt-8">
      <div className="bg-black p-8 rounded-lg shadow-lg w-[500px] h-[400px] flex flex-col justify-between items-center">
        <h2 className="text-white text-2xl font-bold">Đăng Nhập</h2>
        <div className="w-full flex flex-col gap-4">
          <input type="text" placeholder="Tên đăng nhập" className="w-full p-3 border rounded bg-[#202938] text-white placeholder-gray-400" />
          <input type="password" placeholder="Mật khẩu" className="w-full p-3 border rounded bg-[#202938] text-white placeholder-gray-400" />
        </div>

        <button className="bg-[#202938] text-white py-3 px-6 rounded w-full">Đăng Nhập</button>
        <p className="text-white">
          Chưa có tài khoản?{" "}
          <button onClick={() => router.push("/login/Rigister")} className="underline text-white">
            Đăng ký ngay
          </button>
        </p>
      </div>
    </div>
  );
};

export default Login;
