import { productType } from '@/lib/interface';
import React from 'react';
import Link from 'next/link';

const Featured = ({ featuredProducts }: { featuredProducts: productType[] }) => {
    return (
        <section className="flex flex-col items-center mt-12 font-Outfit">
            <p className="text-[1.5rem] md:text-[3rem] font-normal">Thực đơn</p>
            <Link href={'/menu'} className="my-16 font-light text-[1.5rem]">
                Xem thêm
            </Link>
        </section>
    );
};

export default Featured;
