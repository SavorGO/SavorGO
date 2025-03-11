import Link from "next/link";
import React from "react";
import { Button } from "../ui/button";

const Featured = () => {
  return (
    <div className="font-DMSans relative items-center flex flex-col mt-6">
      <p className="text-[1.5rem] md:text-[3rem] font-normal mb-6">Thực đơn</p>
      {/* Thay bannerData.image bằng logo.png */}
      <img
        width={920}
        height={920}
        src="/menu2.jpg"
        alt="Hero banner"
        className="w-full rounded-xl"
      />
      <div className="absolute top-[40%] text-white flex flex-col items-center">
        <p className="md:text-[6rem] text-[1.75rem] font-light text-center">
          {}
        </p>
        <Link href={"setTable"}>
          <Button
            variant={"secondary"}
            className="h-8 mt-2 md:w-52  md:h-14 md:text-[1.5rem] md:mt-4 md:p-4 font-light"
          >
            Xem thực đơn
          </Button>
        </Link>
      </div>
    </div>
  );
};

export default Featured;
