"use client";
import { MenuItemType } from "@/lib/interface";
import Link from "next/link";
import React, { useState } from "react";
import Cart from "../ShoppingCart/Cart";
import { AiOutlineMenu } from "react-icons/ai";
import { IoIosArrowForward, IoIosCloseCircleOutline } from "react-icons/io";
import { Separator } from "@radix-ui/react-separator";

interface MenuProps {
  links: MenuItemType[];
}

interface MenuProps {
  links: MenuItemType[];
}

export const DesktopMenu: React.FC<MenuProps> = ({ links }) => {
  const [selected, setSelected] = useState<string | null>(null);

  return (
    <div className="flex items-center justify-between w-[1170px] border-b-2 border-black pb-2">
      {/* Logo */}
      <Link href="/" className="w-32" onClick={() => setSelected(null)}>
        <img src="/logo6.png" alt="Logo" className="w-36 h-25" />
      </Link>

      {/* Danh sách link */}
      <ul className="flex-grow flex justify-center gap-6">
        {links.map((link: MenuItemType) => (
          <Link
            key={link.id}
            href={link.url}
            className={`text-xl font-medium transition-colors duration-300 
              ${selected === link.id ? "text-red-500" : "text-black"} 
              hover:text-red-500`}
            onClick={() => setSelected(link.id)} // Cập nhật mục đã chọn
          >
            {link.title}
          </Link>
        ))}
      </ul>

      {/* User & Cart */}
      <div className="flex items-center gap-2">
        <Link href="/login/login">
          <img src="/user.png" alt="User" className="w-16 h-16" />
        </Link>
        <Cart />
      </div>
    </div>
  );
};

export const MobileMenu: React.FC<MenuProps> = ({ links }) => {
  const [isOpened, setOpen] = useState(false);

  return (
    <>
      <ul>
        {isOpened ? (
          <div className="overlay">
            <div
              className={`p-8 fixed overflow-hidden left-0 top-0 w-[80%] h-screen z-[10] flex flex-col text-[1.5rem] bg-white text-black`}
            >
              {links.map((link: MenuItemType) => {
                return (
                  <>
                    <li
                      key={link.id}
                      className="flex items-center justify-between"
                    >
                      <Link href={link.url} className="">
                        {link.title}
                      </Link>
                      <IoIosArrowForward />
                    </li>
                    <Separator className="my-2" />
                  </>
                );
              })}
            </div>
            <IoIosCloseCircleOutline
              onClick={() => setOpen(false)}
              size={48}
              color="white"
              className="cursor-pointer fixed top-4 right-4"
            />
          </div>
        ) : (
          <AiOutlineMenu
            onClick={() => setOpen(true)}
            className="cursor-pointer"
            size={24}
          />
        )}
      </ul>
      <Link
        href="/"
        className="object-fit justify-self-center col-start-2 w-24 md:w-36"
      >
        <img src="/logo.png" alt="Logo" className="" />
      </Link>
      <Cart />
      <Separator className="my-4 border-black" />{" "}
      {/* Đường line được thêm vào đây */}
    </>
  );
};
