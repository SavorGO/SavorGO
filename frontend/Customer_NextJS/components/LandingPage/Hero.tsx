import Link from 'next/link';
import React from 'react';
import { Button } from '../ui/button';

const Hero = () => {
  return (
    <div className="font-DMSans relative items-center flex flex-col mt-12">
        {/* Thay bannerData.image bằng logo.png */}
        <img 
          width={920} 
          height={920} 
          src="/hero.png" 
          alt="Hero banner" 
          className="w-full rounded-xl" 
        />
        <div className="absolute top-[40%] text-white flex flex-col items-center">
            <p className="md:text-[6rem] text-[1.75rem] font-light text-center">{}</p>
            <Link href={"setTable"}>
                <Button variant={'secondary'} className="h-10 mt-6 md:w-60  md:h-16 md:text-[1.5rem] md:mt-4 md:p-4 font-light">
                    Đặt bàn
                </Button>
            </Link>
        </div>
    </div>
  );
};

export default Hero;
